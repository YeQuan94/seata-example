FROM java:8
ARG JAR_FILE=target/seata-cluster-goods-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
CMD ["--server.port=8001"]
EXPOSE 8001
ENTRYPOINT ["java","-jar","/app.jar"]
