@echo off
setlocal enabledelayedexpansion

REM Moverse a la carpeta raiz del proyecto
cd ..

REM Crear directorio out si no existe
if not exist out mkdir out

REM Compila todos los archivos .java
javac -d out -cp src src\main\java\org\fernandodev\*.java src\main\java\org\fernandodev\chat_commands\*.java

REM Ejecuta el servidor en una ventana nueva
start cmd /k "java -cp out org.fernandodev.ChatServer"

REM Espera un momento a que inicie el servidor
timeout /t 2 >nul

REM Numero de clientes a lanzar
set CLIENT_COUNT=3

REM Lanzar multiples clientes
for /L %%i in (1,1,%CLIENT_COUNT%) do (
    start cmd /k java -cp out org.fernandodev.ChatClient
)
