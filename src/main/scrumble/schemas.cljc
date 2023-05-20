(ns scrumble.schemas)

(def scramble-in
  [:map
   [:source-string [:re #"^[a-z]+$"]]
   [:sub-string [:re #"^[a-z]+$"]]])
