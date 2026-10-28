#!/bin/sh
# OpenBao en modo desarrollo (Q-18) con un token raíz generado al azar en cada volumen nuevo.
# El token queda en el volumen tmpfs de secretos, legible solo por el vault y por vault-init.
set -eu
umask 077
mkdir -p /run/secretos/vault
if [ ! -s /run/secretos/vault/token-raiz ]; then
  head -c 32 /dev/urandom | od -An -tx1 | tr -d ' \n' > /run/secretos/vault/token-raiz
fi
exec bao server -dev -dev-listen-address=0.0.0.0:8200 -dev-root-token-id="$(cat /run/secretos/vault/token-raiz)"
