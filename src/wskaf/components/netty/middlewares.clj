(ns wskaf.components.netty.middlewares
  "middleware that inject system dependencies, parse json"
  (:require [cheshire.core :as json]
            [wskaf.components.kafka.protocols :refer [ISaveEvent IGetTopic]]
            [wskaf.components.netty.responses :refer [malformed-response]]))

(defn- json-request? [request]
  (when-let [type (get-in request [:headers :content-type])]
    (seq (re-find #"^application/(.+\+)?json" type))))

(defn- read-json [request]
  (when (json-request? request)
    (when-let [body-string (:body request)]
      (try
        [true (json/parse-string body-string)]
        (catch com.fasterxml.jackson.core.JsonParseException ex
          [false nil])))))

(defn wrap-json-body
  "Transform body, from json string to clojure data"
  [handler]
  (fn [request]
    (if-let [[valid? json] (read-json request)]
      (if valid?
        (handler (assoc request :body json))
        malformed-response)
      (handler request))))

(defn wrap-system
  "Inject component into the request map"
  [kafka handler]
  {:pre [(satisfies? IGetTopic kafka) (satisfies? ISaveEvent kafka)]}
  (fn [request]
    (handler (assoc request :kafka kafka))))
