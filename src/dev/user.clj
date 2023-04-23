(ns user
  (:require [scrumble.core :as scrumble]
            reitit.ring.middleware.dev))

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
  (restart!))
