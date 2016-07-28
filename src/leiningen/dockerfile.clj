(ns leiningen.dockerfile
  (:require [clojure.string :as string]
            [leiningen.uberjar :refer [uberjar]]))

(defn dockercmd-str
  [cmd]
  (->> (for [s (if (sequential? cmd) cmd [cmd])] (str "\"" s "\""))
       (string/join ",")
       (format "[%s]")))

(defn env-str
  [envs]
  (->> (for [[k v] envs] (str "ENV " k "=" v))
       (string/join "\n")))

(defn expose-str
  [exposes]
  (->> (for [[c-port h-port] exposes] (str "EXPOSE " c-port " " h-port))
       (string/join "\n")))

(defn files-str
  [files]
  (->> (for [from-to files] (str "COPY " (dockercmd-str from-to)))
       (string/join "\n")))

(defn dockerfile-str
  [{:keys [from-image exposes envs files work-dir instructions entry-point cmd jar-file]}]
  (let [ep (or entry-point ["/usr/bin/java" "-jar" "uberjar.jar"])
        c (or cmd [""])
        wd (or work-dir "/")
        ep-str (dockercmd-str ep)
        cmd-str (dockercmd-str c)]
    (->> (concat [(str "FROM " from-image)]
                 [(expose-str exposes)]
                 [(env-str envs)]
                 [(str "WORKDIR " wd)]
                 [(files-str files)]
                 [(str "COPY " (dockercmd-str [jar-file "uberjar.jar"]))]
                 instructions
                 [(str "ENTRYPOINT " ep-str)]
                 [(str "CMD " cmd-str)])
         (remove empty?)
         (string/join "\n"))))

(defn write-file [contents] (spit "Dockerfile" contents))

(defn dockerfile
  [project & args]
  (let [jar (uberjar project)
        docker-options (merge {:from-image "java:8-jre"
                               :jar-file jar}
                              (:dockerfile project))]
    (write-file (dockerfile-str docker-options))))
