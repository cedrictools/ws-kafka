(ns wskaf.components.kafka.lifecycle
  "Our persistence interaction.  all writes go out to Kafka."
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging      :refer [info warn debug error]]
            [wskaf.components.kafka.protocols :refer [ISaveEvent IGetTopic]])
  (:import [kafka.admin AdminUtils]
           [kafka.utils ZKStringSerializer$]
           [org.I0Itec.zkclient ZkClient]
           [org.apache.kafka.clients.producer ProducerRecord KafkaProducer]
           [java.util Properties]))

(defn- record
  "Create a kafka record."
  [topic data]
  (ProducerRecord. topic
                   (str (java.util.UUID/randomUUID))
                   (when data (pr-str data))))

(defn- kafka-producer
  "Get a kafka producer from a configuration map."
  [opts]
  (let [props (Properties.)]
    (doseq [[k v] opts]
      (.put props (name k) (str v)))
    (KafkaProducer. props)))

(defn- zk-client [config]
  (try
    (ZkClient. (:zookeeper.connect config) 1000 1000 (ZKStringSerializer$/MODULE$))
    (catch Exception e
      (binding [*out* *err*]
        (println "zookeeper connection failed"
                 (:zookeeper.connect config)
                 (.getMessage e)))
      (System/exit 1))))

(defn topic-exists?  [consumer topic]
  {:pre [(satisfies? IGetTopic consumer)]}
  (.-topic? consumer topic))

(defn push [producer topic payload]
  (try
    (do (.save! producer topic  payload) nil)
    (catch Exception e (println e "can't publish"))))

(defrecord Store [config]
  component/Lifecycle
  (start [this]
    (info "start kafka component")
    (assoc this
           :consumer (zk-client (:consumer config))
           :producer (kafka-producer (:producer config))))
  (stop [this]
    (assoc this :consumer nil :producer nil))
  IGetTopic
  (-topic? [this topic]
    (AdminUtils/topicExists (:consumer this) topic))
  ISaveEvent
  (save! [this topic payload]
    (.send (:producer this) (record topic  payload))))

(defn new-kafka-store [config]
  (->Store config))
