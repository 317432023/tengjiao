FROM java:8
ENV dataDir=/data appName=app.jar port=8080
EXPOSE $port
RUN mkdir -p $dataDir
WORKDIR $dataDir
COPY *.jar $dataDir/$appName
CMD ["--server.port=$port"]
ENTRYPOINT ["java","-jar","$appName"]