(ns wskaf.metrics
  (:require [com.stuartsierra.component :as component]
            [metrics.core :refer [new-registry]]
            [metrics.gauges :as gauge :refer [defgauge]]
            [metrics.meters :as meter :refer [defmeter]]))

;@TODO expose metrics

(comment
  (defn dummy []
    (rand-int 1000))

  (defgauge reg topics-available dummy)
  (defmeter reg request-served)

  (defn log-request []
    (meter/mark! request-served))

  (defn report-all []
    {:topics-available (gauge/value topics-available)
     :request-served (meter/rates request-served)
     :topic-list #{"topicA" "topicB"}})

  (defrecord Metrics []
    component/Lifecycle
    (start [this]
      (assoc this :register (new-registry)))
    (stop [this]
      (assoc this :register nil))))
