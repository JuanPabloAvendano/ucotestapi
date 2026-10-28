#!/usr/bin/env bash
# Preparación de Azure, una sola vez (H5.2). La corre Juan con su sesión de `az login`:
#   infra/azure/preparar.sh <dueño/repositorio> <correo-para-alertas>
# Crea el grupo de recursos, el presupuesto con alerta y la identidad con la que GitHub Actions entra a Azure por
# OIDC (credencial federada, sin contraseñas guardadas). Al final imprime las variables que hay que poner en GitHub.
set -euo pipefail

REPO="${1:?uso: preparar.sh <dueño/repositorio> <correo>}"
CORREO="${2:?uso: preparar.sh <dueño/repositorio> <correo>}"
GRUPO="${UCOTEST_GRUPO:-ucotest-demo}"
REGION="${UCOTEST_REGION:-brazilsouth}"
PRESUPUESTO_USD="${UCOTEST_PRESUPUESTO_USD:-10}"

suscripcion=$(az account show --query id -o tsv)
inquilino=$(az account show --query tenantId -o tsv)
echo "Suscripción $suscripcion · grupo $GRUPO · región $REGION"

echo "1/5 Proveedores de recursos"
for p in Microsoft.App Microsoft.OperationalInsights Microsoft.ContainerRegistry Microsoft.KeyVault \
         Microsoft.DBforPostgreSQL Microsoft.Network Microsoft.ManagedIdentity Microsoft.Consumption; do
  az provider register --namespace "$p" --wait -o none
done

echo "2/5 Grupo de recursos"
az group create -n "$GRUPO" -l "$REGION" -o none
alcance=$(az group show -n "$GRUPO" --query id -o tsv)

echo "3/5 Presupuesto mensual de $PRESUPUESTO_USD USD con alerta al 50 % y al 90 % a $CORREO"
inicio=$(date -u +%Y-%m-01)
az rest --method put \
  --url "https://management.azure.com${alcance}/providers/Microsoft.Consumption/budgets/${GRUPO}-presupuesto?api-version=2023-05-01" \
  --body "{\"properties\":{\"category\":\"Cost\",\"amount\":$PRESUPUESTO_USD,\"timeGrain\":\"Monthly\",
           \"timePeriod\":{\"startDate\":\"${inicio}T00:00:00Z\"},
           \"notifications\":{
             \"real50\":{\"enabled\":true,\"operator\":\"GreaterThan\",\"threshold\":50,\"contactEmails\":[\"$CORREO\"]},
             \"real90\":{\"enabled\":true,\"operator\":\"GreaterThan\",\"threshold\":90,\"contactEmails\":[\"$CORREO\"]}}}}" \
  -o none

echo "4/5 Identidad de GitHub Actions con credencial federada (environment azure)"
nombreApp="${GRUPO}-github"
appId=$(az ad app list --display-name "$nombreApp" --query '[0].appId' -o tsv)
[ -n "$appId" ] || appId=$(az ad app create --display-name "$nombreApp" --query appId -o tsv)
az ad sp show --id "$appId" -o none 2>/dev/null || az ad sp create --id "$appId" -o none
spId=$(az ad sp show --id "$appId" --query id -o tsv)
# Los repositorios creados desde el 15-jul-2026 firman el token OIDC con el subject inmutable
# repo:<dueño>@<id-dueño>/<repo>@<id-repo>:...; se arma con los ids que da `gh` (si no hay gh, el formato antiguo).
sujetoRepo="$REPO"
if command -v gh >/dev/null && ids=$(gh api "repos/$REPO" --jq '"\(.owner.login)@\(.owner.id)/\(.name)@\(.id)"' 2>/dev/null); then
  sujetoRepo="$ids"
fi
credencial="{
    \"name\": \"github-azure\",
    \"issuer\": \"https://token.actions.githubusercontent.com\",
    \"subject\": \"repo:${sujetoRepo}:environment:azure\",
    \"audiences\": [\"api://AzureADTokenExchange\"]}"
sujetoActual=$(az ad app federated-credential list --id "$appId" --query "[?name=='github-azure'].subject" -o tsv)
if [ -z "$sujetoActual" ]; then
  az ad app federated-credential create --id "$appId" -o none --parameters "$credencial"
elif [ "$sujetoActual" != "repo:${sujetoRepo}:environment:azure" ]; then
  az ad app federated-credential update --id "$appId" --federated-credential-id github-azure -o none --parameters "$credencial"
fi
echo "  subject: repo:${sujetoRepo}:environment:azure"

echo "5/5 Permisos, solo sobre el grupo de recursos"
# Contributor crea los recursos; User Access Administrator, las asignaciones de rol de base.bicep;
# Key Vault Secrets Officer y AcrPush, los secretos y las imágenes que pone desplegar.sh.
asignar() {
  az role assignment create --assignee-object-id "$1" --assignee-principal-type "$2" \
    --role "$3" --scope "$alcance" -o none 2>/dev/null || true
}
for rol in Contributor "User Access Administrator" "Key Vault Secrets Officer" AcrPush; do
  asignar "$spId" ServicePrincipal "$rol"
done
# Juan también puede desplegar desde su máquina con desplegar.sh.
yo=$(az ad signed-in-user show --query id -o tsv)
for rol in "Key Vault Secrets Officer" AcrPush; do
  asignar "$yo" User "$rol"
done

cat <<FIN

Listo. En GitHub (lo hace Juan), en el repositorio $REPO:
  gh api -X PUT repos/$REPO/environments/azure
  gh variable set AZURE_CLIENT_ID       --repo $REPO --body $appId
  gh variable set AZURE_TENANT_ID       --repo $REPO --body $inquilino
  gh variable set AZURE_SUBSCRIPTION_ID --repo $REPO --body $suscripcion
  gh variable set AZURE_RESOURCE_GROUP  --repo $REPO --body $GRUPO
Para la aprobación manual: Settings → Environments → azure → Required reviewers.
FIN
