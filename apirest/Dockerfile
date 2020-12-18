# Imagen
FROM node:12-alpine

# Contacto
LABEL autor="joseluisgs"
LABEL email="jlgs@cifpvirgendegracia.com"

# Mi directorio de trabajo
WORKDIR /usr/src/app

# Instalo las dependencias, por eso debo compiar los packages
COPY package*.json ./

RUN npm install
# Si tu código es de producción haz..
# RUN npm ci --only=production

# Copiamos el directorio
COPY . .

# Si queremos hacer el build aquí, si no deberíamos copiar solo el directorio /dist
# Que ya está hecho y deberíamos haberlo hecho antes de hacer ejecutar docker
# RUN npm run build

# Exponemos los puertos
EXPOSE 6969

# Ejecutamos
CMD [ "node", "server.js" ]
