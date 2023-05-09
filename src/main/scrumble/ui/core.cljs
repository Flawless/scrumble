(ns scrumble.ui.core
  (:require
   [reagent.dom :as rd]))

(defn interface []
  [:div
   [:h1 "Welcome to scrumble!"]
   [:form
    [:label {:for :source-string} "Scramble Source"]
    [:input {:type 'text :id :source-string :name :source-string}]]])

(defn- render []
  (rd/render [interface]
             (js/document.getElementById "root")))

(defn init []
  (render))

(defn load []
  (render))
