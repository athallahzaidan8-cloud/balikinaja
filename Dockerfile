# ---- tahap 1: build file .jar dengan Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ---- tahap 2: jalankan dengan Java 17 (JRE saja, lebih kecil) ----
FROM eclipse-temurin:17-jre
WORKDIR /app
# font dibutuhkan untuk membuat gambar contoh (DataSeeder)
RUN apt-get update && apt-get install -y --no-install-recommends fontconfig fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*
ENV TZ=Asia/Jakarta
# memori dibatasi agar muat di paket gratis Render (512 MB)
ENV JAVA_OPTS="-Xmx300m -Xss512k -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Duser.timezone=Asia/Jakarta -Djava.awt.headless=true"
COPY --from=build /app/target/balikinaja-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
