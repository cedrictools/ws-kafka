(ns wskaf.components.kafka.protocols)

(defprotocol ISaveEvent
  "save payload into kafka topic"
  (save! [this topic payload] "Upsert entry."))

(defprotocol IGetTopic
  "find kafka topic"
  (-topic? [this topic]  "Return true if topic exists"))
