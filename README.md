# Bullet Drift

Bullet Drift es un juego Java 2D hecho con Swing. Nacio como un proyecto antiguo e incompleto, y ahora se esta recuperando paso a paso para convertirlo en una version mas estable, jugable y facil de ampliar.

## Estado Del Proyecto

El juego actualmente compila y ejecuta. La base jugable ya incluye movimiento, disparo, oleadas, varios tipos de enemigos, puntuacion, HP, vidas, power-ups, pausa, modo debug de hitboxes, fase de llave/portal y un boss final provisional.

Sigue siendo un proyecto en desarrollo: el balance, las sensaciones de combate, la fase final y la estructura interna todavia se estan puliendo.

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
- `ENTER`: reanudar desde pausa o reiniciar tras game over/victoria.
- `R`: reiniciar desde pausa.
- `Q`: salir desde pausa.
- `F1`: activar/desactivar debug de hitboxes.

## Funcionalidades Actuales

- Movimiento del jugador limitado a la pantalla.
- Disparo normal y disparo automatico manteniendo click.
- Enemigos con distintos comportamientos por oleadas.
- Sistema de puntuacion: cada enemigo derrotado da 10 puntos.
- Progresion por oleadas: cada 100 puntos se avanza de oleada.
- Sistema de HP, vidas y game over.
- Invulnerabilidad breve tras recibir dano, con parpadeo visual.
- Pausa con menu de reanudar, reiniciar y salir.
- HUD con puntos, oleada, HP, vidas y power-ups activos.
- Modo debug de hitboxes con `F1`.
- Llave defendible desde la fase final: si los enemigos especiales la destruyen, se pierde.
- Portal y boss final provisional con movimiento, disparos, dano por contacto y pantalla de victoria al derrotarlo.
- Ventana redimensionable con HUD, enemigos, power-ups y hitboxes escalados.
- Scripts de compilacion y ejecucion.

## Enemigos

- `NORMAL`: enemigo basico. Aparece desde la oleada 0.
- `FAST`: enemigo rapido. Aparece desde la oleada 1.
- `TANK`: enemigo mas grande y resistente. Aparece desde la oleada 2.
- `ZIGZAG`: enemigo que baja alternando movimiento horizontal. Aparece desde la oleada 3.
- `CHASER`: enemigo que persigue al jugador con velocidad moderada. Aparece desde la oleada 4.
- `KEY_HUNTER`: enemigo de fase final que va directamente hacia la llave.
- `Boss`: boss final provisional tras usar el portal.

## Power-Ups

- `vida`: suma una vida extra.
- `curacion`: cura 20 HP.
- `escudo`: absorbe una colision con enemigo.
- `disparoRapido`: aumenta temporalmente la cadencia de disparo.
- `invulnerabilidad`: evita temporalmente el dano por enemigos.
- `superVelocidad`: aumenta temporalmente la velocidad del jugador.
- `bombShot`: dispara balas bomba con dano de area.
- `fireShoot`: dispara proyectiles de fuego mas potentes.
- `megaMush`: combina velocidad, invulnerabilidad y disparo rapido.
- `mysteryBox`: activa un power-up aleatorio.
- `iman`: atrae los power-ups hacia el jugador.

Los power-ups tienen aparicion ponderada, feedback visual al recogerse y ya no aparecen en la zona superior de la pantalla para evitar situaciones injustas.

## Fase Final

- Al alcanzar 600 puntos aparece una llave defendible en la zona inferior.
- Desde esa fase pueden aparecer `KEY_HUNTER`, que intentan destruir la llave.
- Al alcanzar 1000 puntos se abre un portal y dejan de aparecer enemigos y power-ups normales.
- Con la llave recogida, tocar el portal inicia el boss final provisional, que dispara y hace dano por contacto.
- Al derrotar al boss, la partida termina con pantalla de victoria.

## Estructura

```text
src/files/boss-access/        Sprites de llave y portal
src/files/enemies/            Sprites de enemigos y boss
src/files/player/             Sprites del jugador y bala normal
src/files/power-ups/          Sprites de power-ups y disparos especiales
src/files/wall-papers/        Fondos del juego
src/bulletdrift/             Clase principal
src/bulletdrift/core/        Coordinacion y estado principal del juego
src/bulletdrift/entities/    Entidades jugables
src/bulletdrift/rendering/   Renderizado de escena, HUD e interfaces
src/bulletdrift/spawning/    Generacion de enemigos y power-ups
src/bulletdrift/systems/     Sistemas de reglas, movimiento y colisiones
build.bat                    Compila el proyecto
run.bat                      Ejecuta el juego
```

Clase principal:

```text
bulletdrift.Main
```

## Roadmap

- Probar y ajustar balance de las primeras oleadas.
- Pulir la fase de llave, portal y boss final.
- Ajustar mejor las hitboxes y sensacion de colisiones.
- Mejorar feedback visual y efectos.
- Anadir sonidos.
- Mejorar menus y estados del juego.
- Empaquetar como `.jar` ejecutable.

## Notas De Desarrollo

El proyecto no usa Maven ni Gradle por ahora. Se mantiene como Java plano para avanzar con cambios pequenos y seguros antes de plantear una migracion a un sistema de build mas completo.
