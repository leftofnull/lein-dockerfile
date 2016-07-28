(ns leiningen.docker
  (:require [clojure.string :as string]
            [leiningen.uberjar :refer [uberjar]]))

(defn dockercmd
  [cmd]
  (->> (for [s (if (sequential? cmd) cmd [cmd])] (str "\"" s "\""))
       (string/join ",")
       (format "[%s]")))

(defn env-strings
  [envs]
  (->> (for [[k v] envs] (str "ENV " k "=" v))
       (string/join "\n")))

(defn expose-str [[c-port h-port]] (str "EXPOSE " c-port " " h-port))
(defn files-str [from-to] (str "COPY " (dockercmd from-to)))

(defn dockerfile
  [{:keys [from-image exposes envs files work-dir instructions entry-point cmd jar-file]}]
  (let [ep (or entry-point ["/usr/bin/java" "-jar" "uberjar.jar"])
        c (or cmd [""])
        wd (or work-dir "/")
        ep-str (dockercmd ep)
        cmd-str (dockercmd c)]
    (->> (concat [(str "FROM " from-image)]
                 [(->>
                   (for [e exposes] (expose-str e))
                   (string/join "\n"))]
                 [(env-strings envs)]
                 [(str "WORKDIR " wd)]
                 [(->>
                   (for [f files] (files-str f))
                   (string/join "\n"))]
                 [(str "COPY " (dockercmd [jar-file "uberjar.jar"]))]
                 instructions
                 [(str "ENTRYPOINT " ep-str)]
                 [(str "CMD " cmd-str)])
         (remove empty?)
         (string/join "\n"))))

(defn write-file [contents] (spit "Dockerfile" contents))

(defn docker
  [project & args]
  (let [jar (uberjar project)
        dockerimage (merge {:from-image "java:8-jre"
                            :jar-file jar}
                           (:docker project))]
    (write-file (dockerfile dockerimage))))
