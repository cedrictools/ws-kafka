(ns wskaf.controllers.events
  (:require [wskaf.components.netty.responses :as resp]
            [wskaf.components.kafka.lifecycle :as db]))

(defn valid-payload? [payload]
  (map? payload))

(defn validate-payload [payload]
  (when-not (valid-payload? payload)
    resp/malformed-response))

(defn topic? [zk topic]
  (db/topic-exists? zk topic))

(defn validate-topic [zk topic]
  (when-not (topic? zk topic)
    {:status 404 :body (str "invalid topic " topic)}))

(defn handle-event [topic payload kafka]
  (or (validate-payload payload)
      (validate-topic kafka topic)
      (db/push kafka topic payload)
      resp/success))
