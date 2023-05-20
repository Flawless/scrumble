(ns scrumble.handler
  (:require
   [clojure.java.io :as io]
   [muuntaja.core :as m]
   [reitit.coercion.malli]
   [reitit.ring :as ring]
   [reitit.ring.coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.util.http-response :as http]
   [scrumble.schemas :as schemas]
   [scrumble.scramble :as scramble]
   [scrumble.ui.index :as ui.index]))

(defn scramble [request]
  (let [{:keys [source-string sub-string]} (-> request :parameters :body)]
    {:scramble? (scramble/scramble? source-string sub-string)}))

(defn routes []
  [["/api" {:name :api}
    ["/ping" {:name :api/ping
              :get (fn [_] (http/ok "pong"))}]
    ["/scramble" {:name :api/scramble
                  :post {:handler (comp http/ok scramble)
                         :parameters {:body schemas/scramble-in}
                         :responses {200 {:body [:map [:scramble? :boolean]]}}}}]]

   ["/" {:name :ui
         :handler (fn [_] (http/ok ui.index/page))}]
   ["/*" {:name :resource
          :handler (ring/create-resource-handler)}]])

(defn- router []
  (ring/router
    (routes)
    {:conflicts (constantly nil)
     :reitit.middleware/transform reitit.ring.middleware.dev/print-request-diffs
     :data {:muuntaja m/instance
            :coercion reitit.coercion.malli/coercion
            :middleware [muuntaja/format-middleware
                         reitit.ring.coercion/coerce-exceptions-middleware
                         parameters/parameters-middleware
                         reitit.ring.coercion/coerce-request-middleware
                         reitit.ring.coercion/coerce-response-middleware]}}))

(defn app []
  (ring/ring-handler
   (router)))
