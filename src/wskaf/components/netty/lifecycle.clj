(ns wskaf.components.netty.lifecycle
  (:require
   [com.stuartsierra.component :as component]
   [clojure.tools.logging      :refer [info warn debug error]]
   [wskaf.components.netty.handler :refer [make-handler]]
   [wskaf.components.netty.server :refer [run-server]]))

(defrecord HttpServer [config kafka]
  component/Lifecycle
  (start [this]
    (info "start http server")
    (let [app (make-handler kafka)
          server  (run-server config app)]
      (info (str "Listening for PUT on "  (:host config) ":" (:port config)))
      (assoc this :stop-fn server)))
  (stop [this]
    (info "stop http server")
    (when-let [stop (:stop-fn this)]
      (stop))
    (assoc this :server nil)))

(defn new-web-server [config]
  (map->HttpServer {:config config}))
