FROM eclipse-temurin:17-jdk
COPY target/ImageUpload-0.0.1-SNAPSHOT.jar ImageUpload-0.0.1-SNAPSHOT.jar
ENTRYPOINT [ "java", "-jar", "ImageUpload-0.0.1-SNAPSHOT.jar" ]