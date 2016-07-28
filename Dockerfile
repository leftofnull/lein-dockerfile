FROM java:8-jre
EXPOSE 80 80
EXPOSE 443 443
ENV PS1=":>"
ENV JAVA_OPTS="-Xmx64g"
WORKDIR foo/
COPY ["resources/config.edn","config.edn"]
COPY ["/Users/akahoun/proj/lein/lein-dockerfile/target/lein-dockerfile-1.0.0-standalone.jar","uberjar.jar"]
RUN sudo apt-get update
RUN sudo apt-get upgrade --all
ENTRYPOINT ["/usr/bin/java","-jar","uberjar.jar"]
CMD ["--help"]