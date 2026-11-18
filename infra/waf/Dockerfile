# WAF de Azure (H5.2): la misma imagen y las mismas exclusiones que el compose, sin montajes.
FROM owasp/modsecurity-crs:4-nginx-alpine-202611090911
COPY exclusiones-antes-de-crs.conf /etc/modsecurity.d/owasp-crs/rules/REQUEST-900-EXCLUSION-RULES-BEFORE-CRS.conf
