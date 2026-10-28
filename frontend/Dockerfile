# Build con Node LTS y servido con Nginx (Q-7). Etiquetas exactas (Q-2).
FROM node:24.22.1-alpine AS construccion
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run type-check && npm run build

FROM nginx:1.31.0-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=construccion /app/dist /usr/share/nginx/html
