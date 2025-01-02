FROM openjdk:23-jdk-slim

# Copia o código-fonte
COPY . .

# Instala o Maven (se necessário)
RUN apt-get update && apt-get install -y maven

# Realiza a construção do projeto
RUN mvn clean install

# Expõe a porta onde o aplicativo será executado
EXPOSE 8080

# Define o comando para rodar a aplicação
COPY target/todolist-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
