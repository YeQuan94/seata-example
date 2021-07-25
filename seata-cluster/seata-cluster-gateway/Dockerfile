FROM java:8
ARG JAR_FILE=target/seata-cluster-gateway-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
CMD ["--server.port=8000"]
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app.jar"]
