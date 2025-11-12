@echo off
echo ========================================
echo   ESTUDA MAIS - Iniciar Servidores
echo ========================================
echo.

cd Frontend\angular-app

echo [1/2] Verificando dependencias...
if not exist node_modules (
    echo Instalando dependencias...
    call npm install
)

echo.
echo [2/2] Iniciando servidores...
echo.
echo Abrindo Terminal 1: json-server (API Backend)
start cmd /k "npm run mock:api"

timeout /t 3 /nobreak > nul

echo Abrindo Terminal 2: Angular Dev Server
start cmd /k "npm start"

echo.
echo ========================================
echo   Servidores iniciados!
echo ========================================
echo.
echo   API Backend:  http://localhost:3000
echo   Angular App:  http://localhost:4200
echo.
echo Pressione qualquer tecla para fechar...
pause > nul
