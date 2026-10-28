#!/usr/bin/env bash
# Despliegue en Azure (H5.2). Lo usa .github/workflows/desplegar.yml y también se puede correr a mano con `az login`:
#   UCOTEST_GRUPO=ucotest-demo infra/azure/desplegar.sh
# Pasos: base → secretos → imágenes → apps → prueba de humo. Se puede repetir: cada paso es idempotente.
set -euo pipefail

GRUPO="${UCOTEST_GRUPO:-ucotest-demo}"
raiz="$(cd "$(dirname "$0")/../.." && pwd)"
ETIQUETA="${UCOTEST_ETIQUETA:-$(git -C "$raiz" rev-parse --short=12 HEAD)}"
azure="$raiz/infra/azure"

echo "1/5 Base (registro, bóveda, red, logs, entorno)"
salida=$(az deployment group create -g "$GRUPO" -n base -f "$azure/base.bicep" --query properties.outputs -o json)
registro=$(jq -r .registroNombre.value <<<"$salida")
servidor=$(jq -r .registroServidor.value <<<"$salida")
boveda=$(jq -r .bovedaNombre.value <<<"$salida")

echo "2/5 Secretos en Key Vault (solo los que faltan; mismos nombres que el vault local, Q-18)"
asegurar() {
  if ! az keyvault secret show --vault-name "$boveda" -n "$1" -o none 2>/dev/null; then
    az keyvault secret set --vault-name "$boveda" -n "$1" --value "$2" -o none
    echo "  creado $1"
  fi
}
# Hexadecimal + mayúsculas: cumple la política de claves de PostgreSQL y no necesita escaparse en JDBC.
aleatorio() { echo "$(openssl rand -hex 20)Zq"; }
asegurar postgres-password "$(aleatorio)"
asegurar keycloak-admin-password "$(aleatorio)"
asegurar rabbitmq-password "$(aleatorio)"
# Clave de los usuarios de demostración del realm, la misma del modo local.
asegurar clave-demo ucotest

echo "3/5 Imágenes con etiqueta $ETIQUETA en $servidor"
az acr login -n "$registro"
"$raiz/mvnw" -B -ntp -q -f "$raiz/pom.xml" -DskipTests package jib:build -Djib.to.image="$servidor/ucotestapi:$ETIQUETA"
construir() {
  docker build -t "$servidor/$1:$ETIQUETA" "$2"
  docker push -q "$servidor/$1:$ETIQUETA"
}
construir ucotest-frontend "$raiz/frontend"
construir ucotest-keycloak "$raiz/infra/keycloak"
construir ucotest-gateway "$raiz/infra/gateway"
construir ucotest-waf "$raiz/infra/waf"
# Imágenes de terceros copiadas al registro: Container Apps no depende del límite de descargas de Docker Hub.
az acr import -n "$registro" --source docker.io/library/rabbitmq:4.4.0-alpine --image rabbitmq:4.4.0-alpine --force -o none

echo "4/5 Apps (PostgreSQL, RabbitMQ, Keycloak, app, frontend, WAF + gateway)"
url=$(az deployment group create -g "$GRUPO" -n apps -f "$azure/apps.bicep" -p etiqueta="$ETIQUETA" \
  --query properties.outputs.urlPublica.value -o tsv)
echo "  URL pública: $url"

echo "5/5 Prueba de humo por el WAF"
for _ in $(seq 60); do
  curl -fsS -o /dev/null "$url/auth/realms/ucotest/.well-known/openid-configuration" \
    && curl -fsS -o /dev/null "$url/" && break
  sleep 10
done
python3 "$azure/humo.py" "$url"
[ -n "${GITHUB_STEP_SUMMARY:-}" ] && echo "Desplegado \`$ETIQUETA\` en $url" >> "$GITHUB_STEP_SUMMARY"
echo "Listo: $url"
