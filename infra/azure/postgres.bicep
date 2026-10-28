// PostgreSQL Flexible Server con las bases de la app y de Keycloak (H5.2).
param location string
param nombre string
@secure()
param claveAdministrador string
// Misma versión mayor que el compose y Testcontainers (Q-2).
param version string = '18'

resource servidor 'Microsoft.DBforPostgreSQL/flexibleServers@2024-08-01' = {
  name: nombre
  location: location
  sku: { name: 'Standard_B1ms', tier: 'Burstable' }
  properties: {
    version: version
    administratorLogin: 'ucotest'
    administratorLoginPassword: claveAdministrador
    storage: { storageSizeGB: 32 }
    backup: { backupRetentionDays: 7, geoRedundantBackup: 'Disabled' }
    highAvailability: { mode: 'Disabled' }
    network: { publicNetworkAccess: 'Enabled' }
  }
}

// Solo servicios de Azure (las salidas de Container Apps); TLS obligatorio por defecto en Flexible Server.
resource soloAzure 'Microsoft.DBforPostgreSQL/flexibleServers/firewallRules@2024-08-01' = {
  parent: servidor
  name: 'AllowAllAzureServicesAndResourcesWithinAzureIps'
  properties: { startIpAddress: '0.0.0.0', endIpAddress: '0.0.0.0' }
}

resource baseApp 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2024-08-01' = {
  parent: servidor
  name: 'ucotest'
  properties: { charset: 'UTF8', collation: 'en_US.utf8' }
}

resource baseKeycloak 'Microsoft.DBforPostgreSQL/flexibleServers/databases@2024-08-01' = {
  parent: servidor
  name: 'keycloak'
  properties: { charset: 'UTF8', collation: 'en_US.utf8' }
  dependsOn: [baseApp]
}

output fqdn string = servidor.properties.fullyQualifiedDomainName
