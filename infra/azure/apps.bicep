// Servicios de S1 en Azure Container Apps (H5.2), el mismo reparto que el compose:
//   internet → WAF (ModSecurity + CRS) → API Gateway (Nginx) → app | Keycloak | frontend;  app → PostgreSQL, RabbitMQ.
// El WAF es la única entrada pública; el gateway corre en la misma app que el WAF y le habla por localhost.
// Los secretos se leen de Key Vault con la identidad administrada; ninguno pasa por aquí en claro.
targetScope = 'resourceGroup'

param location string = resourceGroup().location
@maxLength(12)
param prefijo string = 'ucotest'
@description('Etiqueta de las imágenes propias en el registro (SHA del commit).')
param etiqueta string
param versionPostgres string = '18'

var sufijo = uniqueString(resourceGroup().id)

resource identidad 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' existing = {
  name: '${prefijo}-apps'
}
resource registro 'Microsoft.ContainerRegistry/registries@2023-07-01' existing = {
  name: '${prefijo}${sufijo}'
}
resource boveda 'Microsoft.KeyVault/vaults@2023-07-01' existing = {
  name: take('${prefijo}-${sufijo}', 24)
}
resource entorno 'Microsoft.App/managedEnvironments@2024-03-01' existing = {
  name: '${prefijo}-entorno'
}

module postgres 'postgres.bicep' = {
  name: 'postgres'
  params: {
    location: location
    nombre: '${prefijo}-pg-${sufijo}'
    claveAdministrador: boveda.getSecret('postgres-password')
    version: versionPostgres
  }
}

var imagenes = registro.properties.loginServer
var urlPublica = 'https://${prefijo}-waf.${entorno.properties.defaultDomain}'
// Nombres internos del entorno: cada app con ingreso interno responde en http://<nombre>.
var hostApp = '${prefijo}-app'
var hostKeycloak = '${prefijo}-keycloak'
var hostFrontend = '${prefijo}-frontend'
var hostRabbit = '${prefijo}-rabbitmq'

var identidadApps = {
  type: 'UserAssigned'
  userAssignedIdentities: { '${identidad.id}': {} }
}
var registros = [{ server: imagenes, identity: identidad.id }]

func secretoDeBoveda(boveda string, nombre string, identidad string) object => {
  name: nombre
  keyVaultUrl: '${boveda}secrets/${nombre}'
  identity: identidad
}

resource rabbitmq 'Microsoft.App/containerApps@2024-03-01' = {
  name: hostRabbit
  location: location
  identity: identidadApps
  properties: {
    environmentId: entorno.id
    workloadProfileName: 'Consumption'
    configuration: {
      registries: registros
      secrets: [secretoDeBoveda(boveda.properties.vaultUri, 'rabbitmq-password', identidad.id)]
      ingress: { external: false, transport: 'tcp', targetPort: 5672, exposedPort: 5672 }
    }
    template: {
      containers: [
        {
          name: 'rabbitmq'
          image: '${imagenes}/rabbitmq:4.4.0-alpine'
          resources: { cpu: json('0.5'), memory: '1Gi' }
          env: [
            { name: 'RABBITMQ_DEFAULT_USER', value: 'ucotest' }
            { name: 'RABBITMQ_DEFAULT_PASS', secretRef: 'rabbitmq-password' }
          ]
        }
      ]
      scale: { minReplicas: 1, maxReplicas: 1 }
    }
  }
}

resource keycloak 'Microsoft.App/containerApps@2024-03-01' = {
  name: hostKeycloak
  location: location
  identity: identidadApps
  properties: {
    environmentId: entorno.id
    workloadProfileName: 'Consumption'
    configuration: {
      registries: registros
      secrets: [
        secretoDeBoveda(boveda.properties.vaultUri, 'postgres-password', identidad.id)
        secretoDeBoveda(boveda.properties.vaultUri, 'keycloak-admin-password', identidad.id)
        secretoDeBoveda(boveda.properties.vaultUri, 'clave-demo', identidad.id)
      ]
      ingress: { external: false, targetPort: 8080, allowInsecure: true }
    }
    template: {
      containers: [
        {
          name: 'keycloak'
          image: '${imagenes}/ucotest-keycloak:${etiqueta}'
          resources: { cpu: json('1'), memory: '2Gi' }
          env: [
            { name: 'KC_DB_URL', value: 'jdbc:postgresql://${postgres.outputs.fqdn}:5432/keycloak?sslmode=require' }
            { name: 'KC_DB_USERNAME', value: 'ucotest' }
            { name: 'KC_DB_PASSWORD', secretRef: 'postgres-password' }
            // Issuer único detrás del WAF (A-27, Q-5); las claves se descargan por la red interna.
            { name: 'KC_HOSTNAME', value: '${urlPublica}/auth' }
            { name: 'KC_HOSTNAME_BACKCHANNEL_DYNAMIC', value: 'true' }
            { name: 'KC_PROXY_HEADERS', value: 'xforwarded' }
            { name: 'KC_HTTP_ENABLED', value: 'true' }
            { name: 'KC_BOOTSTRAP_ADMIN_USERNAME', value: 'admin' }
            { name: 'KC_BOOTSTRAP_ADMIN_PASSWORD', secretRef: 'keycloak-admin-password' }
            { name: 'UCOTEST_URL_PUBLICA', value: urlPublica }
            { name: 'UCOTEST_CLAVE_DEMO', secretRef: 'clave-demo' }
          ]
          probes: [
            {
              type: 'Startup'
              httpGet: { path: '/auth/health/ready', port: 9000 }
              initialDelaySeconds: 20
              periodSeconds: 15
              failureThreshold: 10
            }
            {
              type: 'Readiness'
              httpGet: { path: '/auth/health/ready', port: 9000 }
              periodSeconds: 10
            }
          ]
        }
      ]
      scale: { minReplicas: 1, maxReplicas: 1 }
    }
  }
}

resource app 'Microsoft.App/containerApps@2024-03-01' = {
  name: hostApp
  location: location
  identity: identidadApps
  properties: {
    environmentId: entorno.id
    workloadProfileName: 'Consumption'
    configuration: {
      registries: registros
      secrets: [
        secretoDeBoveda(boveda.properties.vaultUri, 'postgres-password', identidad.id)
        secretoDeBoveda(boveda.properties.vaultUri, 'rabbitmq-password', identidad.id)
      ]
      ingress: { external: false, targetPort: 8080, allowInsecure: true }
    }
    template: {
      containers: [
        {
          name: 'app'
          image: '${imagenes}/ucotestapi:${etiqueta}'
          resources: { cpu: json('1'), memory: '2Gi' }
          env: [
            { name: 'SERVER_PORT', value: '8080' }
            { name: 'SPRING_DOCKER_COMPOSE_ENABLED', value: 'false' }
            // En Azure los secretos llegan de Key Vault como variables; OpenBao queda para el modo local.
            { name: 'SPRING_CLOUD_VAULT_ENABLED', value: 'false' }
            { name: 'UCOTEST_DB_URL', value: 'jdbc:postgresql://${postgres.outputs.fqdn}:5432/ucotest?sslmode=require' }
            { name: 'SPRING_DATASOURCE_USERNAME', value: 'ucotest' }
            { name: 'SPRING_DATASOURCE_PASSWORD', secretRef: 'postgres-password' }
            { name: 'UCOTEST_URL_PUBLICA', value: urlPublica }
            { name: 'UCOTEST_JWK_SET_URI', value: 'http://${hostKeycloak}/auth/realms/ucotest/protocol/openid-connect/certs' }
            { name: 'SPRING_RABBITMQ_HOST', value: hostRabbit }
            { name: 'SPRING_RABBITMQ_PORT', value: '5672' }
            { name: 'SPRING_RABBITMQ_USERNAME', value: 'ucotest' }
            { name: 'SPRING_RABBITMQ_PASSWORD', secretRef: 'rabbitmq-password' }
            // Igual que el compose: RabbitMQ se usa desde S4 y todavía no cuenta para la salud de la app.
            { name: 'MANAGEMENT_HEALTH_RABBIT_ENABLED', value: 'false' }
          ]
          probes: [
            {
              type: 'Startup'
              httpGet: { path: '/actuator/health', port: 8080 }
              initialDelaySeconds: 15
              periodSeconds: 10
              failureThreshold: 10
            }
            {
              type: 'Readiness'
              httpGet: { path: '/actuator/health', port: 8080 }
              periodSeconds: 10
            }
          ]
        }
      ]
      scale: { minReplicas: 1, maxReplicas: 2 }
    }
  }
  dependsOn: [keycloak, rabbitmq]
}

resource frontend 'Microsoft.App/containerApps@2024-03-01' = {
  name: hostFrontend
  location: location
  identity: identidadApps
  properties: {
    environmentId: entorno.id
    workloadProfileName: 'Consumption'
    configuration: {
      registries: registros
      ingress: { external: false, targetPort: 80, allowInsecure: true }
    }
    template: {
      containers: [
        {
          name: 'frontend'
          image: '${imagenes}/ucotest-frontend:${etiqueta}'
          resources: { cpu: json('0.25'), memory: '0.5Gi' }
        }
      ]
      scale: { minReplicas: 1, maxReplicas: 2 }
    }
  }
}

resource waf 'Microsoft.App/containerApps@2024-03-01' = {
  name: '${prefijo}-waf'
  location: location
  identity: identidadApps
  properties: {
    environmentId: entorno.id
    workloadProfileName: 'Consumption'
    configuration: {
      registries: registros
      // Única entrada pública, con HTTPS de Container Apps.
      ingress: { external: true, targetPort: 8080, transport: 'auto', allowInsecure: false }
    }
    template: {
      containers: [
        {
          name: 'waf'
          image: '${imagenes}/ucotest-waf:${etiqueta}'
          resources: { cpu: json('0.5'), memory: '1Gi' }
          env: [
            { name: 'BACKEND', value: 'http://localhost:8081' }
            { name: 'PORT', value: '8080' }
            // Modo bloqueo con paranoia 1 (Q-6), igual que el compose.
            { name: 'MODSEC_RULE_ENGINE', value: 'On' }
            { name: 'BLOCKING_PARANOIA', value: '1' }
          ]
        }
        {
          name: 'api-gateway'
          image: '${imagenes}/ucotest-gateway:${etiqueta}'
          resources: { cpu: json('0.25'), memory: '0.5Gi' }
          env: [
            { name: 'UCOTEST_APP_HOST', value: hostApp }
            { name: 'UCOTEST_KEYCLOAK_HOST', value: hostKeycloak }
            { name: 'UCOTEST_FRONTEND_HOST', value: hostFrontend }
          ]
        }
      ]
      scale: { minReplicas: 1, maxReplicas: 2 }
    }
  }
  dependsOn: [app, frontend, keycloak]
}

output urlPublica string = urlPublica
