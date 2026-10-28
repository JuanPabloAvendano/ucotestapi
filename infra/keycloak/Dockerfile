# Keycloak de Azure (H5.2): modo producción (`start`) contra PostgreSQL, con el realm dentro de la imagen.
# El compose local sigue con `start-dev` y el realm montado; este archivo solo lo usa infra/azure/desplegar.sh.
FROM quay.io/keycloak/keycloak:26.8.1
ENV KC_DB=postgres \
    KC_HEALTH_ENABLED=true \
    KC_HTTP_RELATIVE_PATH=/auth
COPY realm-ucotest.json /opt/keycloak/data/import/realm-ucotest.json
RUN /opt/keycloak/bin/kc.sh build
# --import-realm no sobrescribe: si el realm ya existe en la base, lo deja como está.
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--optimized", "--import-realm"]
