(ns uplift.views.add
  (:require [uplift.views.base :as base]
            [hiccup.form :refer :all]
            [hiccup.core]))

(declare lift)

(defn exercise [[lift-type lifts]]
  [:div.row [:div.small-12.columns [:h1.lift-type lift-type]
   (map lift lifts)]])

(defn weight-input
  ([] (weight-input ""))
  ([value] [:input {:type "number"
                    :min 0
                    :step "5"
                    :placeholder "weight"
                    :value value}]))

(defn reps-input
  ([] (reps-input ""))
  ([value] [:input {:type "number"
                    :min 1
                    :placeholder "reps"
                    :value value}]))

(defn sets-input
  ([] (sets-input ""))
  ([value] [:input
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
   [:div.small-6.columns.small-offset-3
    [:input.lift-type {:type "text"
                       :placeholder "Lift Name"}]]])

(defn get-page [{:keys [user activities]}]
  (base/base {:content [:div
                        (map exercise activities)
                        (new-activity)]
              :top-links (base/links-for user)
              :current "/add"}))
