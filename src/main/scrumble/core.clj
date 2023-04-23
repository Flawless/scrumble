(ns scrumble.core
  (:require
   [ring.adapter.jetty :as jetty]
   [scrumble.handler :as handler]))

(defn run-server! [{:keys [host port]}]
  (jetty/run-jetty handler/app {:host host
                                :port port
                                :join? false}))
