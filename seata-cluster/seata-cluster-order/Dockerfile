FROM java:8
ARG JAR_FILE=target/seata-cluster-order-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
CMD ["--server.port=8002"]
EXPOSE 8002
ENTRYPOINT ["java","-jar","/app.jar"]
