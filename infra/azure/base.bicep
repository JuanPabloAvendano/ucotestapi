// Base de Azure (H5.2): lo que no depende de las imágenes. Registro, bóveda, red, logs y entorno de Container Apps.
// Se aplica antes de construir las imágenes: el registro tiene que existir para recibirlas.
targetScope = 'resourceGroup'

param location string = resourceGroup().location
@maxLength(12)
param prefijo string = 'ucotest'

// Nombres globales (registro y bóveda) con sufijo estable por grupo de recursos; apps.bicep los calcula igual.
var sufijo = uniqueString(resourceGroup().id)
var nombreRegistro = '${prefijo}${sufijo}'
var nombreBoveda = take('${prefijo}-${sufijo}', 24)

// Roles integrados de Azure (identificadores fijos).
var rolAcrPull = '7f951dda-4ed3-4680-a7ca-43fe172d538d'
var rolLectorDeSecretos = '4633458b-17de-408a-b874-0445c86b69e6'

// Identidad de las apps: solo lee secretos y descarga imágenes (permisos mínimos).
resource identidad 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: '${prefijo}-apps'
  location: location
}

resource logs 'Microsoft.OperationalInsights/workspaces@2023-09-01' = {
  name: '${prefijo}-logs'
  location: location
  properties: {
    sku: { name: 'PerGB2018' }
    retentionInDays: 30
  }
}

resource registro 'Microsoft.ContainerRegistry/registries@2023-07-01' = {
  name: nombreRegistro
  location: location
  sku: { name: 'Basic' }
  properties: {
    adminUserEnabled: false
  }
}

// Mismos nombres de secretos que el vault local (Q-18); los crea desplegar.sh si faltan.
resource boveda 'Microsoft.KeyVault/vaults@2023-07-01' = {
  name: nombreBoveda
  location: location
  properties: {
    tenantId: subscription().tenantId
    sku: { family: 'A', name: 'standard' }
    enableRbacAuthorization: true
    softDeleteRetentionInDays: 7
  }
}

resource identidadDescargaImagenes 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(registro.id, identidad.id, rolAcrPull)
  scope: registro
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', rolAcrPull)
    principalId: identidad.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

resource identidadLeeSecretos 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(boveda.id, identidad.id, rolLectorDeSecretos)
  scope: boveda
  properties: {
    roleDefinitionId: subscriptionResourceId('Microsoft.Authorization/roleDefinitions', rolLectorDeSecretos)
    principalId: identidad.properties.principalId
    principalType: 'ServicePrincipal'
  }
}

// Red propia: RabbitMQ usa ingreso TCP, que en Container Apps exige un entorno con red virtual.
resource red 'Microsoft.Network/virtualNetworks@2024-05-01' = {
  name: '${prefijo}-red'
  location: location
  properties: {
    addressSpace: { addressPrefixes: ['10.40.0.0/16'] }
    subnets: [
      {
        name: 'container-apps'
        properties: {
          addressPrefix: '10.40.0.0/23'
          delegations: [
            {
              name: 'container-apps'
              properties: { serviceName: 'Microsoft.App/environments' }
            }
          ]
        }
      }
    ]
  }
}

resource entorno 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: '${prefijo}-entorno'
  location: location
  properties: {
    workloadProfiles: [
      { name: 'Consumption', workloadProfileType: 'Consumption' }
    ]
    vnetConfiguration: {
      infrastructureSubnetId: red.properties.subnets[0].id
      internal: false
    }
    appLogsConfiguration: {
      destination: 'log-analytics'
      logAnalyticsConfiguration: {
        customerId: logs.properties.customerId
        sharedKey: logs.listKeys().primarySharedKey
      }
    }
  }
}

output registroNombre string = registro.name
output registroServidor string = registro.properties.loginServer
output bovedaNombre string = boveda.name
output entornoDominio string = entorno.properties.defaultDomain
