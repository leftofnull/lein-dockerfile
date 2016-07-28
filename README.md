# lein-dockerfile

[![Clojars Project](https://img.shields.io/clojars/v/lein-dockerfile.svg)](https://clojars.org/lein-dockerfile)

A Leiningen plugin to make a Dockerfile for the project.

## Usage

Use this for user-level plugins:

Put `[lein-dockerfile "1.0.0"]` into the `:plugins` vector of your `:user`
profile.

Use this for project-level plugins:

Put `[lein-dockerfile "1.0.0"]` into the `:plugins` vector of your project.clj.

```
$ lein dockerfile
```

Without any customization you will end up with a `Dockerfile` like:

```
FROM java:8-jre
WORKDIR /
COPY ["/full/path/to/your/project/target/foo-0.0.1-SNAPSHOT-standalone.jar","uberjar.jar"]
ENTRYPOINT ["/usr/bin/java","-jar","uberjar.jar"]
CMD [""]
```

### Customization

Inside your project.clj add a root level key `:docker`.

_NOTE: All of these are optional_

```
(defproject foo "0.0.1-SNAPSHOT"
  :dockerfile {:work-dir "foo/"
               :envs [["PS1" "\":>\""]
                      ["JAVA_OPTS" "\"-Xmx64g\""]]
               :exposes [[80 80]
                         [443 443]]
               :from-image "java:8-jre"
               :files [["resources/config.edn" "config.edn"]]
               :instructions ["RUN sudo apt-get update"
                              "RUN sudo apt-get upgrade --all"]
               :entry-point ["/usr/bin/java" "-jar" "uberjar.jar"]
               :cmd ["--help"]})
```

This example would result in a `Dockerfile` like:

```
FROM java:8-jre
EXPOSE 80 80
EXPOSE 443 443
ENV PS1=":>"
ENV JAVA_OPTS="-Xmx64g"
WORKDIR foo/
COPY ["resources/config.edn","config.edn"]
COPY ["/full/path/to/your/project/target/foo-0.0.1-SNAPSHOT-standalone.jar","uberjar.jar"]
RUN sudo apt-get update
RUN sudo apt-get upgrade --all
ENTRYPOINT ["/usr/bin/java","-jar","uberjar.jar"]
CMD ["--help"]
```

## License

Copyright © 2016 Left of Null

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
