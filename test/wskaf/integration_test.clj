(ns wskaf.integration-test
  (:use midje.sweet)
  (:require [clj-http.client :as client]
            [com.stuartsierra.component :as component]
            [wskaf.components.netty.lifecycle :refer [new-web-server]]
            [cheshire.core :as json])
  (:import [wskaf.components.kafka.protocols ISaveEvent,IGetTopic]))

(defrecord Kafka [config]
  component/Lifecycle
  (start [this] this)
  (stop [this] this)
  IGetTopic
  (-topic? [this topic] (#{"topicA"} topic))
  ISaveEvent
  (save! [this topic payload] nil))

(defn test-system []
  (component/system-map
   :config {:http 9999}
   :kafka (map->Kafka {})
   :app (component/using (new-web-server {:host "localhost" :port 9999}) [:kafka])))

(def server (atom nil))

(defn start-server []
  (reset! server (component/start-system (test-system))))

(defn stop-server []
  (when @server (component/stop-system @server)))

(against-background [(before :contents (start-server))
                     (after :contents (stop-server))]
                    (fact "404 is served at root"
                          (let [response (client/get "http://localhost:9999" {:throw-exceptions false})]
                            (:status response) => 404))

                    (fact "publish on kafka valid topic return 200"
                          (let [response (client/put "http://localhost:9999/v1/produce/topicA"
                                                     {:throw-exceptions false
                                                      :content-type :json
                                                      :body (json/generate-string {:event-id 321
                                                                                   :map "with some data"})})]
                            (:status response) => 200))

                    (fact "publish on kafka invalid topic return 404"
                          (let [response (client/put "http://localhost:9999/v1/produce/topicB"
                                                     {:throw-exceptions false
                                                      :content-type :json
                                                      :body (json/generate-string {:event-id 321
                                                                                   :map "with some data"})})]
                            (:status response) => 404)))
