"use strict";

var _interopRequireDefault = require("@babel/runtime/helpers/interopRequireDefault");

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports["default"] = void 0;

var _jsonServer = _interopRequireDefault(require("json-server"));

var _path = _interopRequireDefault(require("path"));

// Librerías
// si quieres cambiar el path de tu bd o nombre de fichero json.
// Lo tengo puesto en la raiz para que sea accesible desde
// https://my-json-server.typicode.com/joseluisgs/APIRESTFake
// es una BD Limitada, la completa la tienes en el el directorio BD y es la que usa este servidir
const router = _jsonServer["default"].router(_path["default"].join(__dirname, '/bd/db.json')); // const router = jsonServer.router('db.json') // BD Limitada
// Middleware por defecto


const middlewares = _jsonServer["default"].defaults(); // Configuramos el puerto


const port = process.env.PORT || 6969;
/**
 * Clase servidor
 */

class Server {
  // iniciamos el servidor
  constructor() {
    // Costruimos el servidor
    this.server = _jsonServer["default"].create();
    this.server.use(middlewares);
    this.server.use(router);
  } // Método de inicio


  start() {
    this.instancia = this.server.listen(port, () => {
      if (process.env.NODE_ENV !== 'test') {
        console.log("\u2691 Servidor JSON funcionando \u2713 -> http://localhost:".concat(port));
        console.log('⚑ Fake API REST por joseluisgs ✓ -> https://github.com/joseluisgs/APIRESTFake');
      }
    });
    return this.instancia; // Devolvemos la instancia del servidor
  } // Cierra el servidor


  close() {
    // Desconectamos el socket server
    this.instancia.close();

    if (process.env.NODE_ENV !== 'test') {
      console.log('▣  Servidor parado');
    }
  }

}
/**
 * Devuelve la instancia de conexión siempre la misma, simulamos un singleton
 */


const server = new Server(); // Exportamos la variable por li la queremos usar en otros módulos, por ejemplo los test

var _default = server; // Si ningun fichero está haciendo un import y ejecutando ya el servidor, lo lanzamos nosotros

exports["default"] = _default;

if (!module.main) {
  server.start();
}