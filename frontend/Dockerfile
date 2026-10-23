# ==============================================================================
# ESTÁGIO 1: Build da Aplicação Web (Node 20 + Angular CLI)
# Compila o código-fonte TypeScript/Angular para arquivos estáticos otimizados
# ==============================================================================
FROM node:20-alpine AS builder

WORKDIR /build

# Copia os manifestos de pacotes para aproveitar o cache de dependências do Docker
COPY package.json package-lock.json ./
RUN npm ci

# Copia o código-fonte do Frontend e executa o build de produção
COPY . .
RUN npm run build

# ==============================================================================
# ESTÁGIO 2: Servidor Web e Proxy Reverso (Nginx Alpine)
# Serve o bundle estático e redireciona chamadas /api/ para o backend-service
# ==============================================================================
FROM nginx:alpine

# Remove a configuração padrão do Nginx
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copia os arquivos estáticos compilados no Estágio 1 (outputPath do angular.json)
COPY --from=builder /build/dist/blackcat-front/browser/ /usr/share/nginx/html/

# Copia a configuração customizada com Proxy Reverso para o backend-service:8080
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
