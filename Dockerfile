# Use official Gradle image with JDK 21
FROM gradle:8.6-jdk21-jammy

# Set working directory
WORKDIR /app

# Enable Gradle caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle
RUN ./gradlew --no-daemon dependencies

# Copy source code (will be mounted as volume in dev)
COPY src src

# Development-specific configurations
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

# Expose ports
EXPOSE 8080

CMD ["./gradlew", "bootRun", "--continuous", "--build-cache"]