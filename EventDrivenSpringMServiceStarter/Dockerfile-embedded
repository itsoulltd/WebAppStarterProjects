FROM openjdk:8
MAINTAINER lab.infoworks.com

RUN mkdir -m 755 -p /home/downloads
ADD target/ROOT.jar ROOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ROOT.jar"]
