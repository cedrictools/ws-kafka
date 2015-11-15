(ns wskaf.components.system
  (:require
   [com.stuartsierra.component :as component]
   [unilog.config              :refer [start-logging!]]
   [clojure.tools.logging      :refer [info warn debug error]]
   [wskaf.config     :as config]
   [wskaf.components.kafka.lifecycle :refer [new-kafka-store]]
   [wskaf.components.netty.lifecycle :refer [new-web-server]]))

(defn new-system [path quiet]
  (let [config (config/read-path (or path "doc/config.yaml"))]
    (start-logging! (:logging config))
    (component/system-map
     :kafka (new-kafka-store (:kafka config))
     :http (component/using (new-web-server (:http config)) [:kafka]))))
