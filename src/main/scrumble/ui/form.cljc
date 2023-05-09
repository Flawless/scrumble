(ns scrumble.ui.form
  (:require
   [malli.core :as m]
   [malli.transform :as mt]
   [meander.epsilon :as meps]
   [meander.rewrite.epsilon :as mre]
   [scrumble.schemas :as schemas]))

(def malli-field
  [:map {:closed true}
   [:id  [:string {:min 1}]]
   [:name [:string {:min 1}]]
   [:class [:string {:min 1}]]
   [:description [:string]]
   [:error-message [:string]]
   [:default-error-message [:string]]
   [:type [:enum
           "text"
           "password"
           "checkbox"
           "email"
           "number"
           "file"
           "radio"
           "button"
           "color"
           "image"]]])

(def malli-form
  [:map {:closed true}
   [:fields [:vector malli-field]]
   [:validation-behavior [:enum "on-change" "grouped" "on-submit"]]
   [:class [:string {:min 1}]]
   [:schema [:vector :any]]])

(def reagent-form
  [:any])

(defn matcher [{:keys [pattern expression]}]
  (eval `(fn [data#]
           (let [~'data data#]
             ~(mre/compile-rewrites-args
               (list 'data pattern expression)
               nil)))))

(defn coercer [schema transformer]
  (let [valid? (m/validator schema)
        decode (m/decoder schema transformer)
        explain (m/explainer schema)]
    (fn [x]
      (let [value (decode x)]
        (when-not (valid? value)
          (m/-fail! ::invalid-input {:value value
                                     :schema schema
                                     :explain (explain value)}))
        value))))

(defn transformer [{:keys [registry mappings]} source-transformer target-transformer]
  (let [{:keys [source target]} mappings]
    (comp (coercer (get registry target) target-transformer)
          (matcher mappings)
          (coercer (get registry source) source-transformer))))

(def transformation
  {:pattern '{:fields [{:id !id
                        :name !name
                        :class !class
                        :description !description
                        :error-message !error-message
                        :default-error-message !default-error-message
                        :type !type
                        :value !value
                        :error? !error}
                       ...]
              :class !form-class}
   :expression '[:form {:class !form-class} .
                 [:div {:class !class}
                  [:label {:for !id}]
                  [:span {:visibility (meps/app #(if % :visible :hidden) !error)}
                   !error-message]
                  [:input {:id "id"
                           :type !type
                           :name !name}
                   !value]]
                 ...
                 [:button "Submit"]]})

(defn form [form state]
  (let [assoc-field-values #(update % :fields (partial map (fn [{:keys [id] :as field}]
                                                             (let [id (keyword id)
                                                                   value (get state id)]
                                                               (cond-> field
                                                                 (some? value) (assoc :value value))))))
        schemas {:form/state (:schema form)
                 :form/form malli-form
                 :form/reagent reagent-form}
        form->reagent (transformer {:registry schemas
                                    :mappings (assoc transformation
                                                     :source :form/form
                                                     :target :form/reagent)}
                                   (mt/transformer {})
                                   (mt/transformer {}))]
    {:form (form->reagent form)
     :state {}}))

(form sample-form-datamap nil)

(def transform
  (matcher transformation))

(comment
  (require '[meander.rewrite.epsilon :as mre])

[:form
     [:label {:for "source-string"}]
     (when-let [error (-> state :error-messages :source-string)]
       [:span error])
     [:input {:type "text"
              :id "source-string"
              :name "source-string"}
      (-> state :values :source-string)]

     [:label {:for "source-string"}]
     (when-let [error (-> state :error-messages :sub-string)]
       [:span error])
     [:input {:type "text"
              :id "source-string"
              :name "source-string"}
      (-> state :values :sub-string)]

     [:button {:on-click submit}]]

  (def sample-data
    {:source-string "abc"
     :sub-string "bca"})

  (m/validate malli-field {:id "source-string"
               :name "source-string"
               :class "form-input"
               :description "Contains scramble source characters."
               :error-message "Only small letters allowed."
               :default-error-message "String should not be empty."
               :type "text"})

  (def sample-form-datamap
    {:fields [{:id "source-string"
               :name "source-string"
               :class "form-input"
               :description "Contains scramble source characters."
               :error-message "Only small letters allowed."
               :default-error-message "String should not be empty."
               :type "text"}
              {:id "sub-string"
               :name "sub-string"
               :class "form-input"
               :description "Contains string, that will be scrambled."
               :error-message "Only small letters allowed."
               :default-error-message "String should not be empty."
               :type "text"}]
     :class "input-form"
     :validation-behavior "grouped"
     :schema schemas/scramble-in})

  (m/validate malli-form sample-form-datamap)

  (transform sample-form-datamap)

  (meps/rewrites sample-form-datamap
                 {:fields [{:id !id
                            :class !class}]}
                 [[:input {:id !id
                           :class !class}]])

  (meps/rewrites {:name "entity1"
                  :status :complete
                  :history [{:value 100} {:value 300} {:value 700}]
                  :future [{:value 1000} {:value 10000}]}
                 {:name ?name
                  :status ?status
                  :history (meps/scan {:value ?value})
                  :future [{:value !values} ...]}
                 [:div
                  [:h3 ?name]])

  (meps/rewrite {:name "entity1"
                 :status :complete
                 :history [{:value 100} {:value 300} {:value 700}]
                 :future [{:value 1000} {:value 10000}]}
                {:name ?name
                 :status ?status
                 :history [{:value !values} ...]
                 :future [{:value !values} ...]}
                [{:name ?name
                  :status ?status
                  :value !values} ...])

  (meps/rewrite {:name "entity1"
                 :status :complete
                 :history [{:value 100} {:value 300} {:value 700}]
                 :future [{:value 1000} {:value 10000}]}
                {:name ?name
                 :status ?status
                 :history [{:value !values} ...]
                 :future [{:value !values} ...]}
                [{:name ?name
                  :status ?status
                  :value !values} ...])

  (def form-state
    {:values {:source-string ""
              :sub-string "bca"}
     :error-messages {:source-string "some error text"}})

  (gen-form sample-form-datamap form-state)
  ;; =>
  (def sample-form-reagent
    [:form
     [:label {:for "source-string"}]
     (when-let [error (-> state :error-messages :source-string)]
       [:span error])
     [:input {:type "text"
              :id "source-string"
              :name "source-string"}
      (-> state :values :source-string)]

     [:label {:for "source-string"}]
     (when-let [error (-> state :error-messages :sub-string)]
       [:span error])
     [:input {:type "text"
              :id "source-string"
              :name "source-string"}
      (-> state :values :sub-string)]

     [:button {:on-click submit}]]))

(defn gen-form [datamap state]
  (into [:form]
        (mapv (fn [{:keys [id class]}]
                (let [id-key (keyword id)]
                  [:div {:class class}
                   [:label {:for id}]
                   (when-let [error (-> state :error-messages id-key)]
                     [:span error])
                   [:input {:type "text"
                            :id "source-string"
                            :name "source-string"}
                    (-> state :values id-key)]]))
              datamap)))
