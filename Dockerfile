FROM eclipse-temurin:17-jdk-alpine
# HAPUS BARIS INI: VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]