(ns scrumble.ui.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   ["react" :as react]
   [clojure.string :as s]
   [cljs-http.client :as http]
   [clojure.core.async :refer [<!]]
   [reagent.dom :as rd]
   [reagent.core :as r]))

(defn- label [{:keys [id]} label]
  [:label {:for id
           :style {:font-size "1.25rem"
                   :font-weight "600"}}
   (str label ":")])

(defn- input [{:keys [title on-save on-stop input-ref]}]
  (let [val (r/atom title)]
    (fn [{:keys [id placeholder error]}]
      (let [stop (fn [_e]
                   (when on-stop (on-stop)))
            save (fn [e]
                   (let [v (-> @val str s/trim)]
                     (when-not (empty? v)
                       (on-save v))
                     (stop e)))]
        [:input {:type "text"
                 :style (cond-> {:display "block"
                                 :margin "1rem 0"
                                 :padding "8px 12px"
                                 :border "solid 2px black"
                                 :width "100%"}
                          error (assoc :border "solid 2px red"))
                 :value @val
                 :ref input-ref
                 :id id
                 :placeholder placeholder
                 :on-blur save
                 :on-change (fn [e]
                              (reset! val (-> e .-target .-value)))
                 :on-key-down (fn [e]
                                (case (.-which e)
                                  13 (save e)
                                  27 (stop e)
                                  nil))}]))))

(defn- edit [{:keys [id] :as opts}]
  [label {:id id} "Scramble Source"]
  [input (assoc opts :input-ref (react/useRef))])

(defn dissoc-field-error [errors field]
  (let [fields-error (some-> errors
                             :fields
                             (dissoc field))]
    (when (seq fields-error)
      (assoc errors :fields fields-error))))


(defn interface []
  (let [scramble? (r/atom nil)
        scramble-form (r/atom {:source-string ""
                               :sub-string ""})

        errors (r/atom nil)
        $card {:background-color "hsl(79deg 54% 51%)"
               :padding "16px"
               :border-radius "8px"
               :border "solid 8px hsl(0deg 0% 8%)"
               :max-width "500px"
               :margin "auto"
               :margin-bottom "32px"}]
    (fn []
      [:div {:style {:display "flex"
                     :flex-flow "column"
                     :height "100%"}}
       [:header {:style {:flex "0 1 auto"
                         :background-color "hsl(255deg, 90%, 41%)"
                         :border-bottom "solid 8px hsl(0deg 0% 8%)"
                         :color "white"
                         :padding "16px"}}
        [:h2 {:style {:margin "auto"
                      :font-family "'Trebuchet MS', sans-serif"
                      :width "fit-content"}}
         "Welcome to scrumble!"]]
       [:div {:style {:flex "1 1 auto"
                      :padding "16px"
                      :background-color "hsl(0deg 0% 16%)"
                      :font-family "'Trebuchet MS', sans-serif"}}
        [:div {:style $card}
         [:form
          [:f> edit {:id :source-string
                     :error (-> @errors :fields :source-string)
                     :title (:source-string @scramble-form)
                     :on-save (fn [v]
                                (reset! scramble? nil)
                                (swap! scramble-form assoc :source-string v)
                                (swap! errors dissoc-field-error :source-string))
                     :placeholder "rekqodlw"}]
          [:f> edit {:id :sub-string
                     :error (-> @errors :fields :sub-string)
                     :title (:sub-string @scramble-form)
                     :on-save (fn [v]
                                (reset! scramble? nil)
                                (swap! scramble-form assoc :sub-string v)
                                (swap! errors dissoc-field-error :sub-string))
                     :placeholder "world"}]]
         [:button {:style {:width "100%"
                           :font-size "1.25rem"
                           :font-weight "700"
                           :padding "8px 12px"
                           :margin "1rem 0"
                           :background-color "white"
                           :border "solid 4px"}
                   :on-click (fn []
                               (go
                                 (let [{:keys [status body]} (<!
                                                              (http/post
                                                               "/api/scramble"
                                                               {:edn-params @scramble-form
                                                                :headers {"Accept" "application/edn"}}))]
                                   (case status
                                     200 (reset! scramble? (:scramble? body))
                                     400 (reset! errors (if-some [errors (:humanized body)]
                                                          {:fields errors
                                                           :checked? true}
                                                          {:checked? false}))))))}
          (cond
            @errors "Error"

            (some? @scramble?)
            (if @scramble?
              "Scramble!"
              "Not scramble ):")

            :else
            "Check!")]]]])))

(defn- render []
  (rd/render [interface] (js/document.getElementById "root")))

(defn init []
  (render))

(defn load []
  (render))
