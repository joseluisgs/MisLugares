# Mis Lugares 2020

2DAM PMYDM App para poner en práctica todo lo visto en el primer trimestre de 2020.

[![Android](https://img.shields.io/badge/App-Android-g)](https://www.android.com/intl/es_es/)
[![Kotlin](https://img.shields.io/badge/Code-Kotlin-blue)](https://kotlinlang.org/)
[![LICENSE](https://img.shields.io/badge/Lisence-MIT-green)](https://github.com/joseluisgs/RetrofitCRUD2020/blob/master/LICENSE)
![GitHub](https://img.shields.io/github/last-commit/joseluisgs/MisLugaresKotlinRealm)

## Descripción

Sencilla app para trabajar con todos los contenidos desarrollados a lo largo del primer trimestre.
En esta App manejaremos conceptos como diseño de interfaces dinámicas, almacenamiento en teléfono, base de datos y el uso
de sensores y elementos de tu móvil Android como pueden ser la cámara y el GPS.

A lo largo del curso avanzaremos y complementaremos el proyecto con distintas tecnologías en sus distintas versiones.

Es importante, antes de abordar esta App, que revises los proyectos realizados en este curso. En definitiva, este es
un proyecto integrador que resume lo trabajado este año

### Versiones y revisiones
Las versiones no están pensadas para que el alumnado maneje distintas técnicas, no buscando la ideal que dependerá de siempre del problema y la tecnología.s 
- **v0.4.0**: Esta versión trabajamos conjuntamente con imágenes en base de datos y en ficheros usando en path.
A partir de esta versión cambiaremos esta forma y solo trabajaremos con las imágenes dentro de la base de Datos Realm
- **v0.5.0**: Igual que la anterior, pero sin ficheros, todo en Realm.
con el objetivo de unificar sistemas para las próximas versiones.
- **v1.0.0**: Versión donde el almacenamiento de todos los datos se hace de manera local usando una base de datos Realm. 
Las imágenes se usan usando el sistema de codificación en Base64. Esta manera no es muy correcta, pero puede ser útil si
nos enfrentamos a servicios remotos donde desconocemos como subir la imagen. Este sistema tiene el problema de que sobre carga 
la base de datos y no es muy recomendado salvo excepciones como esta.

#### Referencias
Se destacan las siguientes tecnologías usadas, cuyos enlaces son los mismos que hemos utilizado como apuntes en clase y 
están en otras prácticas en mi [GitHub](https://github.com/joseluisgs).

- [Android](https://developer.android.com/docs)
- [Kotlin](https://kotlinlang.org/)
- [Interfaz de Usuario](https://developer.android.com/guide/topics/ui)
- [Imágenes y gráficos](https://developer.android.com/guide/topics/graphics)
- [Audio y vídeo](https://developer.android.com/guide/topics/media)
- [Bases de Datos Realm.io](https://realm.io/docs/kotlin/latest/)
- [Uso de cámara](https://developer.android.com/training/camera)
- [Sistemas de almacenamiento](https://developer.android.com/guide/topics/data)
- [Manejo de permisos](https://developer.android.com/guide/topics/permissions/overview)
- [Uso de Intents](https://developer.android.com/guide/components/intents-common?hl=es)
- [Geolocalización](https://developer.android.com/training/location)
- [Google Maps](https://developers.google.com/maps/documentation/android-sdk/intro)
- [Uso de preferencias](https://developer.android.com/training/data-storage/shared-preferences?hl=es)
- [Tareas en segundo plano](https://developer.android.com/guide/background)
- [Funciones de Voz](https://developer.android.com/training/wearables/apps/voice)
- [Animaciones](https://developer.android.com/training/animation/overview)
- [Json con Gson](https://github.com/google/gson)
- [Códigos QR](https://github.com/zxing/zxing)
- [Permisos con Dexter](https://github.com/Karumi/Dexter)
- [Imágenes con Picasso](https://square.github.io/picasso/)
- [Sesiones y Toekens](https://programacionymas.com/blog/jwt-vs-cookies-y-sesiones)

#### Cosideraciones para ver los mapas
Los mapas hace uso de Google Map Api Key, es por ello que debes activar la clave de la API y activarla para tu proyecto, 
pues puede que varíe a la huella del mio, o que simplemente yo haya desactivado la mía (te recuerdo que es un proyecto para finn docente y lo activo y desactivo sobre la marcha).
Por favor sigue [este tutorial](https://developers.google.com/maps/documentation/android-sdk/get-api-key?hl=es-419) para que puedas ver tus mapas con tu clave.

Recuerda cambiar el fichero Manifest y añadir:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<meta-data android:name="com.google.android.geo.API_KEY"
android:value="@string/google_maps_key" />
 ```      
Te recomiendo que mires la pestaña Run, pues si falla el mapa te dirá si no se ha podido identificar correctamente con la clave de tu API generada.. En el modo debug se hace en ese fichero con la huella SHA-1 y se pone, en el Modo Release,
se debe generar con keytool la huella SHA-1 con los datos del paquete Release, crear un proyecto y subirla
https://developers.google.com/maps/documentation/android-sdk/get-api-key  

#### Herramientas usadas
Estas son las herramientas que más hemos usado en clase para la realización de este proyecto:
<p align="center">
   <img src="https://logodownload.org/wp-content/uploads/2015/05/android-logo-7-1.png" 
    height="45">
    <img src="https://upload.wikimedia.org/wikipedia/commons/b/b5/Kotlin-logo.png" 
    height="45">
     <img src="https://resources.jetbrains.com/storage/products/intellij-idea/img/meta/intellij-idea_logo_300x300.png" 
      height="45">
  <img src="https://miro.medium.com/max/650/1*zzvdRmHGGXONZpuQ2FeqsQ.png" 
  height="45">
  <img src="https://cdn.iconscout.com/icon/free/png-256/github-153-675523.png" 
  height="45">
   <img src="https://user-images.githubusercontent.com/17736615/30980083-f7f8a860-a43c-11e7-939e-f6717a2210fe.png" 
  height="45">
  </p>


## Autor
[José Luis González Sánchez](https://twitter.com/joseluisgonsan) 

[![Twitter](https://img.shields.io/twitter/follow/joseluisgonsan?style=social)](https://twitter.com/joseluisgonsan) 
[![GitHub](https://img.shields.io/github/followers/joseluisgs?style=social)](https://github.com/joseluisgs)

## Licencia

Este proyecto esta licenciado bajo licencia **MIT**, si desea saber más, visite el fichero 
[LICENSE](https://github.com/joseluisgs/MisLugaresKotlinRealm/blob/master/LICENSE) para su uso docente y educativo.
