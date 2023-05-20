(ns user
  (:require
   reitit.ring.middleware.dev
   [scrumble.core :as scrumble]))

(defonce srv (atom nil))

(defn start! []
  (reset! srv (scrumble/run-server! {:host "0.0.0.0"
                                     :port 8081})))

(defn stop! []
  (.stop @srv)
  (reset! srv nil))

(defn restart! []
  (stop!)
  (start!))

(when-not @srv
  (start!))

(comment
  (restart!)
  ;;
  )
