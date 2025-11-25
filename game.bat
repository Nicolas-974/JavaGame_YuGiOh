@echo off
REM Script de lancement direct du jeu Yu-Gi-Oh
REM Double-cliquez sur ce fichier pour lancer le jeu

title Yu-Gi-Oh! - Lancement du jeu

setlocal enabledelayedexpansion

set FXPATH=.\javafx\windows\javafx-sdk-17.0.17\lib
set OUTDIR=out
set RESDIR=src\main\resources
set SRCDIR1=src\main\java
set SRCDIR2=src\loader

REM Vérifier que JavaFX existe
if not exist "%FXPATH%" (
    echo [ERREUR] Chemin JavaFX introuvable: %FXPATH%
    echo.
    echo Verifiez que JavaFX est bien installe dans ce dossier.
    pause
    exit /b 1
)

REM Nettoyer et créer le dossier de sortie
echo ========================================
echo    YU-GI-OH! - COMPILATION DU JEU
echo ========================================
echo.
echo [1/4] Nettoyage du dossier de sortie...
if exist "%OUTDIR%" rmdir /S /Q "%OUTDIR%"
mkdir "%OUTDIR%"

REM Générer la liste des sources
echo [2/4] Generation de la liste des sources...
del /f /q sources.txt 2>nul
for /R "%SRCDIR1%" %%f in (*.java) do (
    echo %%f>>sources.txt
)
for /R "%SRCDIR2%" %%f in (*.java) do (
    echo %%f>>sources.txt
)

if not exist sources.txt (
    echo [ERREUR] Aucune source trouvee !
    pause
    exit /b 1
)

REM Compiler
echo [3/4] Compilation de toutes les sources...
javac --module-path "%FXPATH%" --add-modules javafx.controls,javafx.media -cp "lib\gson-2.10.1.jar" -d "%OUTDIR%" @sources.txt
if %errorlevel% neq 0 (
    echo.
    echo [ERREUR] Echec de la compilation !
    echo Consultez les messages ci-dessus pour plus de details.
    pause
    exit /b %errorlevel%
)

REM Copier les ressources
echo [4/4] Copie des ressources...
xcopy /E /I /Y "%RESDIR%" "%OUTDIR%" >nul

REM Lancer le jeu
echo.
echo ========================================
echo    LANCEMENT DU JEU...
echo ========================================
echo.
java --module-path "%FXPATH%" --add-modules javafx.controls,javafx.media -cp "%OUTDIR%;lib\gson-2.10.1.jar" ui.MainMenu

if %errorlevel% neq 0 (
    echo.
    echo [ERREUR] Le jeu s'est arrete de maniere inattendue.
    pause
    exit /b %errorlevel%
)

endlocal