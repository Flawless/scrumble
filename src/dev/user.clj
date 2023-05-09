(ns user
  (:require
   reitit.ring.middleware.dev
   [scrumble.core :as scrumble]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.devtools.server :as server]))

(defn cljs-repl
  "Entrypoint for cider-jack-in-cljs;
   starts the shadow-cljs watcher and opens the ClojureScript REPL."
  []
  (let [build (keyword (System/getProperty "shadow-build"))]
    (server/start!)
    (shadow/watch build)
    (shadow/nrepl-select build)))

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
