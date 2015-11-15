(ns wskaf.main
  (:gen-class)
  (:require
   [com.stuartsierra.component :as component]
   [unilog.config              :refer [start-logging!]]
   [clojure.tools.logging      :refer [info]]
   [clojure.tools.cli          :refer [cli]]
   [wskaf.components.system :as system]))

(defn get-cli
  "Call cli parsing with our known options"
  [args]
  (try
    (cli args
         ["-h" "--help" "Show help" :default false :flag true]
         ["-f" "--path" "Configuration file path" :default nil]
         ["-q" "--quiet" "Suppress output" :default false :flag true])
    (catch Exception e
      (binding [*out* *err*]
        (println "Could not parse arguments: " (.getMessage e)))
      (System/exit 1))))

(defmacro sig-handler
  [signal & body]
  `(sun.misc.Signal/handle
    (sun.misc.Signal. (-> ~signal name .toUpperCase))
    (proxy [sun.misc.SignalHandler] [] (handle [sig#] ~@body))))

(defn -main
  "Our main function, parses args and launches appropriate services"
  [& args]
  (let [[{:keys [path help quiet]} args banner] (get-cli args)]
    (when help
      (println banner)
      (System/exit 0))
    (let [system  (system/new-system path quiet)]
      (info "installing signal handlers")
      (sig-handler :term
                   (info "caught SIGTERM, quitting")
                   (component/stop-system system)
                   (info "all components shut down")
                   (System/exit 0))
      (sig-handler :hup
                   (info "caught SIGHUP, reloading")
                   (component/stop-system system))
      (info "ready to start system")
      (component/start-system system)))
  nil)
