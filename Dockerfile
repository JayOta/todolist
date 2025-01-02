# Usando a imagem OpenJDK 23 diretamente para o build
FROM openjdk:23-jdk-slim AS BUILD

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Copiar o código para dentro do container
COPY . .

# Rodar o Maven para compilar o projeto
RUN mvn clean install

# Usando a mesma imagem OpenJDK para o estágio final
FROM openjdk:23-jdk-slim

# Expõe a porta onde o app vai rodar
EXPOSE 8080

# Copia o arquivo JAR gerado para a imagem final (use o arquivo todolist-1.0.0.jar)
COPY --from=BUILD /target/todolist-1.0.0.jar app.jar

# Define o comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
