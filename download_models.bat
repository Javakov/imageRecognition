@echo off
setlocal enabledelayedexpansion

REM Directory inside the project where tessdata files will be stored.
set "TARGET_DIR=%~dp0src\main\resources\tessdata"

if not exist "%TARGET_DIR%" (
    echo Creating tessdata directory at "%TARGET_DIR%"
    mkdir "%TARGET_DIR%"
)

call :download "eng" "https://github.com/tesseract-ocr/tessdata_best/raw/main/eng.traineddata"
call :download "rus" "https://github.com/tesseract-ocr/tessdata_best/raw/main/rus.traineddata"

echo All requested models downloaded.
goto :eof

:download
set "MODEL_NAME=%~1"
set "MODEL_URL=%~2"
set "DEST_FILE=%TARGET_DIR%\%MODEL_NAME%.traineddata"

echo.
echo Downloading %MODEL_NAME% model...
powershell -Command "Invoke-WebRequest -Uri '%MODEL_URL%' -OutFile '%DEST_FILE%'" >nul
if exist "%DEST_FILE%" (
    echo %MODEL_NAME% saved to %DEST_FILE%
) else (
    echo Failed to download %MODEL_NAME%. Please check your internet connection and rerun this script.
)
goto :eof

