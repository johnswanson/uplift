(ns uplift.views.add
  (:require [uplift.views.base :as base]
            [hiccup.form :refer :all]
            [hiccup.core]))

(declare lift)

(defn exercise [[lift-type lifts]]
  [:div.exercise [:div.row [:div.small-12.columns [:h1.lift-type (name lift-type)]]]
   (map lift lifts)
   [:div.row
    [:div.small-2.columns.small-offset-5
     [:button.add-lift
      [:i.icon-plus-sign]]]]])

(defn weight-input
  ([] (weight-input ""))
  ([value] [:input.add.weight
            {:type "number"
             :min 0
             :step "5"
             :placeholder "weight"
             :value value}]))

(defn reps-input
  ([] (reps-input ""))
  ([value] [:input.add.reps
            {:type "number"
             :min 1
             :placeholder "reps"
             :value value}]))

(defn sets-input
  ([] (sets-input ""))
  ([value] [:input.add.sets
            {:type "number"
             :min 1
             :placeholder "sets"
             :value value}]))

(defn lift [[weight reps sets]]
  [:div.row
   [:div.small-12.columns
    [:div.row
     [:div.small-4.columns
      (weight-input weight)]
     [:div.small-4.columns
      (reps-input reps)]
     [:div.small-4.columns
      (sets-input sets)]]]])

(defn new-activity []
  [:div.row
   [:div.small-6.small-offset-3.columns
    [:div.row.collapse
     [:div.small-10.columns
      [:input.lift-type {:type "text" :placeholder "Lift Name"}]]
     [:div.small-2.columns
      [:a.button.postfix.new-lift "New Lift"]]]]])

(defn get-page [{:keys [user activities]}]
  (base/base {:content [:div
                        [:div#activities (map exercise activities)]
                        [:div#new-activity (new-activity)]]
              :top-links (base/links-for user)
              :current "/add"}))
