@echo off
setlocal enabledelayedexpansion

set PROJECT_DIR=%~dp0

if "%JAVA_HOME%"=="" (
    for %%d in (
        "%PROJECT_DIR%..\..\D:\Work\jdk\jdk-17.0.18+8"
        "D:\Work\jdk\jdk-17.0.18+8"
        "C:\Program Files\Eclipse Adoptium\jdk-17*"
        "C:\Program Files\Java\jdk-17*"
    ) do (
        if exist "%%d\bin\java.exe" (
            set JAVA_HOME=%%d
            echo [auto] JAVA_HOME=!JAVA_HOME!
        )
    )
)

if "%JAVA_HOME%"=="" (
    echo [ERROR] JAVA_HOME not found. Please:
    echo   1. Create D:\Work\jdk\ and place JDK 17 there
    echo   2. OR set JAVA_HOME manually
    pause
    exit /b 1
)

set WRAPPER_JAR=%PROJECT_DIR%.mvn\wrapper\maven-wrapper.jar
if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper JAR...
    powershell -Command "Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar' -OutFile '%WRAPPER_JAR%'" -UseBasicParsing
    if not exist "%WRAPPER_JAR%" (
        echo [ERROR] Download failed. Check your network.
        pause
        exit /b 1
    )
)

"%JAVA_HOME%\bin\java.exe" %MAVEN_OPTS% -jar "%WRAPPER_JAR%" %*
exit /b %errorlevel%
