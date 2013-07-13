(ns uplift.views.add
  (:require [uplift.views.base :as base]
            [hiccup.form :refer :all]
            [hiccup.core]))

(declare lift)

(def fake-data
  [{:lift-name "squat"
    :lifts [{:weight 135 :reps 5 :sets 3}
            {:weight 155 :reps 5 :sets 3}]}
   {:lift-name "deadlift"
    :lifts [{:weight 135 :reps 5 :sets 1}
            {:weight 155 :reps 5 :sets 1}]}])

(defn exercise [{:keys [lift-name lifts]}]
  [:div.row [:div.small-12.columns [:h1 lift-name]
   (map lift lifts)]])

(defn weight-input [value] [:input {:type "number"
                                    :min 0
                                    :step "5"
                                    :placeholder "weight"
                                    :value value}])

(defn reps-input [value] [:input {:type "number"
                                  :min 1
                                  :placeholder "reps"
                                  :value value}])

(defn sets-input [value] [:input
                          {:type "number"
                           :min 1
                           :placeholder "sets"
                           :value value}])

(defn lift [{:keys [weight reps sets]}]
  [:div.row
   [:div.small-12.columns
    [:div.row
     [:div.small-4.columns
      (weight-input weight)]
     [:div.small-4.columns
      (reps-input reps)]
     [:div.small-4.columns
      (sets-input sets)]]]])

(defn get-page [{:keys [user]}]
  (base/base {:content (map exercise fake-data)
              :top-links (base/links-for user)
              :current "/add"}))
