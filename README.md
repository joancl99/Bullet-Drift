# Bullet Drift

Bullet Drift es un juego Java 2D hecho con Swing. Nacio como un proyecto antiguo e incompleto, y ahora se esta recuperando paso a paso para convertirlo en una version mas estable, jugable y facil de ampliar.

## Estado Del Proyecto

El juego actualmente compila y ejecuta. La base jugable ya incluye movimiento, disparo, enemigos, puntuacion, vidas, power-ups, pausa y modo debug de hitboxes.

Sigue siendo un proyecto en desarrollo: las colisiones, el balance y la estructura interna todavia se estan puliendo.

## Requisitos

- Java JDK instalado.
- Probado con `javac 17`.
- Windows para usar los scripts `.bat` incluidos.

## Como Compilar Y Ejecutar

Desde la raiz del proyecto:

```powershell
.\build.bat
.\run.bat
```

Tambien se puede hacer manualmente:

```powershell
javac -d out src/bulletdrift/Main.java src/bulletdrift/core/*.java src/bulletdrift/entities/*.java src/bulletdrift/rendering/*.java src/bulletdrift/spawning/*.java src/bulletdrift/systems/*.java
java -cp out bulletdrift.Main
```

## Controles

- `WASD` o flechas: mover jugador.
- Click izquierdo: disparar.
- Mantener click izquierdo: disparo automatico.
- `ESC`: pausar o reanudar.
- `ENTER`: reanudar desde pausa o reiniciar tras game over.
- `Q`: salir desde pausa.
- `F1`: activar/desactivar debug de hitboxes.

## Funcionalidades Actuales

- Movimiento del jugador limitado a la pantalla.
- Disparo normal y disparo automatico manteniendo click.
- Enemigos que aparecen desde la parte superior.
- Sistema de puntuacion por enemigos destruidos.
- Sistema de vidas y game over.
- Invulnerabilidad breve tras recibir dano, con parpadeo visual.
- Pausa con menu de reanudar/salir.
- HUD con puntos, vidas y power-ups activos.
- Modo debug de hitboxes con `F1`.
- Scripts de compilacion y ejecucion.

## Power-Ups

- `vida`: suma una vida.
- `escudo`: absorbe una colision con enemigo.
- `disparoRapido`: aumenta temporalmente la cadencia de disparo.

Los power-ups tienen feedback visual al recogerse y ya no aparecen en la zona superior de la pantalla para evitar situaciones injustas.

## Estructura

```text
Images/                      Recursos graficos
src/bulletdrift/             Clase principal
src/bulletdrift/core/        Coordinacion principal del juego
src/bulletdrift/entities/    Entidades jugables
src/bulletdrift/rendering/   Renderizado de HUD e interfaces
src/bulletdrift/spawning/    Generacion de entidades
src/bulletdrift/systems/     Sistemas de reglas de juego
build.bat                    Compila el proyecto
run.bat                      Ejecuta el juego
```

Clase principal:

```text
bulletdrift.Main
```

## Roadmap

- Ajustar mejor las hitboxes y sensacion de colisiones.
- Limpiar constantes y numeros magicos.
- Mejorar feedback visual y efectos.
- Anadir sonidos.
- Anadir oleadas de enemigos y dificultad progresiva.
- Crear tipos de enemigos distintos.
- Mejorar menus y estados del juego.
- Empaquetar como `.jar` ejecutable.

## Notas De Desarrollo

El proyecto no usa Maven ni Gradle por ahora. Se mantiene como Java plano para avanzar con cambios pequenos y seguros antes de plantear una migracion a un sistema de build mas completo.
