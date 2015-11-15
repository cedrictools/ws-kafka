(ns wskaf.components.netty.handler
  (:require [wskaf.controllers.events :as e]
            [wskaf.components.netty.middlewares :as m]
            [wskaf.components.netty.responses :as resp]
            [compojure.core :refer [defroutes PUT]]
            [compojure.route :refer [not-found]]))

(defroutes api-routes
  (PUT ["/v1/produce/:topic"] [topic :as {payload :body kafka :kafka}]
    (e/handle-event topic payload kafka))
  (not-found resp/not-found))

(defn make-handler [kafka]
  (->> api-routes m/wrap-json-body (m/wrap-system kafka)))
