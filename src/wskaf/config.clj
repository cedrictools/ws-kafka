(ns wskaf.config
  (:require [clojure.java.io :as io]
            [clj-yaml.core :as yaml]
            [clojure.tools.logging      :refer [info]]
            [com.stuartsierra.component :as component]))

(def serializer
  "org.apache.kafka.common.serialization.StringSerializer")

(def default-config
  "Default configuration"
  {:http    {:port 8080
             :host "127.0.0.1"}
   :kafka   {:producer
             {:bootstrap.servers "localhost:9092"
              :value.serializer serializer
              :key.serializer serializer}
             :consumer
             {:zookeeper.connect "localhost:2181"}}
   :logging {:pattern "%p [%d] %t - %c - %m%n"
             :external false
             :console true
             :files  []
             :level  "info"}})

(defn read-yaml
  "Read yaml config file"
  [config-path]
  (info "read config" config-path)
  (if (.exists (io/file config-path))
    (try
      (-> config-path slurp yaml/parse-string)
      (catch Exception ex
        (println "Invalid config file, path:" config-path)))))

(defn read-path [path]
  {:pre [(string? path)]
   :post [(map? %)]}
  (let [yaml-conf (read-yaml path)]
    (merge default-config yaml-conf)))
