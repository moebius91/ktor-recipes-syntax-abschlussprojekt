# Verwenden Sie ein OpenJDK 20 Image als Basis
FROM openjdk:20

# Setzen Sie das Arbeitsverzeichnis im Container
WORKDIR /app

# Kopieren Sie die JAR-Datei in das Container-Verzeichnis
# Achten Sie auf den relativen Pfad zur JAR-Datei
#COPY ../build/libs/com.example.ktor-test-api-all.jar /app/
COPY de.jnmultimedia.ktor-recipes-syntax-abschlussprojekt-all.jar /app/

# Startbefehl für den Ktor-Server
CMD ["java", "-jar", "/app/de.jnmultimedia.ktor-recipes-syntax-abschlussprojekt-all.jar"]


# build befehl für das Terminal:
# cp ../build/libs/de.jnmultimedia.ktor-recipes-syntax-abschlussprojekt-all.jar de.jnmultimedia.ktor-recipes-syntax-abschlussprojekt-all.jar && docker build -t ktor-recipes-syntax-abschlussprojekt .

# Linux:
# cp ../build/libs/de.jnmultimedia.ktor-recipes-syntax-abschlussprojekt-all.jar de.jnmultimedia.ktor-recipes-syntax-abschlussprojekt-all.jar && sudo docker build -t ktor-recipes-syntax-abschlussprojekt .

# Shadowjar erstellt?
# ./gradlew shadowJar
# Im Hauptordner
# Nicht ausführbar?
# chmod +x gradlew