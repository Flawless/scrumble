(ns scrumble.handler
  (:require
   [muuntaja.core :as m]
   [reitit.coercion.malli]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as rrc]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.util.http-response :as http]
   [scrumble.schemas :as schemas]
   [scrumble.scramble :as scramble]))

(defn scramble [request]
  (let [{:keys [source-string sub-string]} (-> request :parameters :form)]
    {:scramble? (scramble/scramble? source-string sub-string)}))

(defn routes []
  [["/api" {:name :api}
    ["/ping" {:name :api/ping
              :get (fn [_] (http/ok "pong"))}]
    ["/scramble" {:name :api/scramble
                  :post {:handler (comp http/ok scramble)
                         :parameters {:body schemas/scramble-in}
                         :responses {200 {:body [:map [:scramble? :boolean]]}}}}]]])

(defn app []
  (ring/ring-handler
   (ring/router
    (routes)
    {:data {:muuntaja m/instance
            :coercion reitit.coercion.malli/coercion
            :middleware [muuntaja/format-middleware
                         parameters/parameters-middleware
                         rrc/coerce-exceptions-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]}})
   (ring/create-resource-handler {:path "/"})
   {:middleware []}))
