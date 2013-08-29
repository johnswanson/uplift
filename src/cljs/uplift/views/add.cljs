(ns uplift.views.add
  (:require-macros
    [dommy.macros :refer [deftemplate]]))

(deftemplate lift-body [type]
  [:div.lift.row
   [:div.small-12.columns
    [:label "ID" [:input.id]]
    [:label "Type" [:input.type {:value type}]]
    [:label "Weight" [:input.weight]]
    [:label "Sets" [:input.sets]]
    [:label "Reps" [:input.reps]]
    [:label "Date" [:input.date]]]])

(deftemplate activity-template [activity-name]
  [:div.lift.row
   [:div.small-6.small-offset-3.columns
    [:h3 activity-name]
    [:a.button.new-lift-body]]])
