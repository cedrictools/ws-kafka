(ns wskaf.components.netty.handler-test
  (:use [midje.sweet]
        [wskaf.controllers.events]
        [wskaf.components.netty.handler])
  (:require [wskaf.components.kafka.lifecycle :as db]))

(defn put
  "utility fn for testing compojure route"
  [resource params]
  (api-routes {:request-method :put :uri resource :body params}))

(against-background
 [(validate-topic ...zk... "ABC") => true]
 (fact "route validates payload"
       (-> (put "/v1/produce/ABC" nil) :status) => 400))

(against-background
 [(validate-payload {:a 1}) => nil
  (db/topic-exists? nil "qux") => nil]
 (fact  "route returns 404 when topic is invalid"
        (-> (put "/v1/produce/qux" {:a 1}) :status) => 404)>)

(against-background
 [(topic? nil "ABC")=> false]
 (fact "route returns 400 when payload invalid"
       (-> (put "/v1/produce/ABC" nil) :status) => 400))

(against-background
 [(validate-payload {}) => nil
  (db/topic-exists? nil "ABC") => true
  (db/push nil "ABC" {}) => nil]
 (fact "route calls kafka handler with correct args"
       (-> (put "/v1/produce/ABC" {}) :status) => 200))


