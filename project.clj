(defproject wskaf "0.1.0"
  :description "Example web service bound to kafka"
  :url "https://github.com/cedrictools/wskaf"
  :aot :all
  :main wskaf.main
  :dependencies
  [[org.clojure/clojure            "1.7.0"]
   [org.clojure/tools.logging      "0.3.1"]
   [org.clojure/tools.cli          "0.3.3"]
   [org.apache.kafka/kafka_2.10    "0.8.2.2" :exclusions [[log4j][org.slf4j/slf4j-log4j12]]]
   [org.apache.kafka/kafka-clients "0.8.2.2"]
   [com.stuartsierra/component     "0.3.0"]
   [clj-yaml                       "0.4.0"]
   [spootnik/unilog                "0.7.12"]
   [io.netty/netty-all             "4.0.33.Final"]
   [cheshire                       "5.5.0"]
   [compojure                      "1.4.0"]
   [metrics-clojure "2.5.1"]]
  :profiles
  {:test {:dependencies [[midje    "1.8.2"]
                         [clj-http "2.0.0"]]
          :plugins [[lein-midje "3.2"]]}})
