# API Gateway de Azure (H5.2). La imagen oficial de Nginx sustituye las variables de la plantilla al arrancar y,
# con NGINX_ENTRYPOINT_LOCAL_RESOLVERS, deja en NGINX_LOCAL_RESOLVERS el DNS del entorno de Container Apps.
FROM nginx:1.30.6-alpine
ENV NGINX_ENTRYPOINT_LOCAL_RESOLVERS=1
COPY azure.conf.template /etc/nginx/templates/default.conf.template
