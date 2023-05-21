(ns scrumble.ui.core
  (:require
   [reagent.dom :as rd]
   [scrumble.ui.view :as view]))

(defn- render []
  (rd/render [view/interface] (js/document.getElementById "root")))

(defn init []
  (render))

(defn load []
  (render))
