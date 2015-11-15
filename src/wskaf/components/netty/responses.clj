(ns wskaf.components.netty.responses
  "http response map")

(def malformed-response
  {:status  400
   :headers {"Content-Type" "text/plain"}
   :body    "Request body is not a valid JSON"})

(def success
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "OK"})

(def not-found
  {:status 404
   :headers {"Content-Type" "application/json"}
   :body    "404"})
