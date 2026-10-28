#!/bin/sh
# Llena el vault con secretos generados al azar (Q-18) y los entrega a cada servicio en su propio directorio,
# como haría una plantilla de Vault Agent. La app no recibe contraseñas: recibe un token de solo lectura y
# lee sus secretos del vault con Spring Cloud Vault.
set -eu
export BAO_ADDR=http://vault:8200
BAO_TOKEN="$(cat /run/secretos/vault/token-raiz)"
export BAO_TOKEN

aleatorio() {
  head -c 48 /dev/urandom | base64 | tr -dc 'A-Za-z0-9' | head -c 32
}

if ! bao kv get -mount=secret infra >/dev/null 2>&1; then
  postgres="$(aleatorio)"
  bao kv put -mount=secret infra \
    postgres-password="$postgres" \
    keycloak-admin-password="$(aleatorio)" \
    rabbitmq-password="$(aleatorio)" \
    clave-demo=ucotest >/dev/null
  # Las claves son nombres de propiedad de Spring: Spring Cloud Vault las publica tal cual.
  bao kv put -mount=secret ucotest \
    spring.datasource.username=ucotest \
    spring.datasource.password="$postgres" >/dev/null
fi

bao policy write ucotest-app - >/dev/null <<'POLITICA'
path "secret/data/ucotest" { capabilities = ["read"] }
path "secret/data/ucotest/*" { capabilities = ["read"] }
path "secret/data/application" { capabilities = ["read"] }
path "secret/data/application/*" { capabilities = ["read"] }
POLITICA

leer() {
  bao kv get -mount=secret -field="$1" infra
}

umask 022
mkdir -p /run/secretos/app /run/secretos/postgres /run/secretos/keycloak /run/secretos/rabbitmq
umask 033
bao token create -policy=ucotest-app -period=24h -field=token > /run/secretos/app/spring.cloud.vault.token
leer postgres-password > /run/secretos/postgres/password
printf 'KC_BOOTSTRAP_ADMIN_USERNAME=admin\nKC_BOOTSTRAP_ADMIN_PASSWORD=%s\nUCOTEST_CLAVE_DEMO=%s\n' \
  "$(leer keycloak-admin-password)" "$(leer clave-demo)" > /run/secretos/keycloak/keycloak.env
printf 'RABBITMQ_DEFAULT_USER=ucotest\nRABBITMQ_DEFAULT_PASS=%s\n' "$(leer rabbitmq-password)" \
  > /run/secretos/rabbitmq/rabbitmq.env
echo "Vault listo: secretos generados y entregados."
