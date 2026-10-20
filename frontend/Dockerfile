FROM nginx:alpine

# Remove a configuração padrão do Nginx
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copia os arquivos estáticos compilados da aplicação web (Angular)
COPY ./blackcat-front/browser/ /usr/share/nginx/html/

# Copia a configuração customizada com Proxy Reverso para o backend-service
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]