(ns uplift.views.add
  (:require [dommy.core :as dommy]
            [uplift.js-utils :as utils])
  (:require-macros [dommy.macros :refer [deftemplate sel1]]))

(deftemplate lift-template [{:keys [weight reps sets editing? local-id] :as lift}]
  [:div.lift
   (if editing?
     (list [:label "Weight" [:input.weight {:data-local-id local-id
                                            :value weight}]]
           [:label "Sets" [:input.sets {:data-local-id local-id
                                        :value sets}]]
           [:label "Reps" [:input.reps {:data-local-id local-id
                                        :value reps}]])
     (list [:div [:span.weight
                  weight]]
           [:div [:span.sets {:data-local-id local-id}]]
           [:div [:span.reps {:data-local-id local-id}]]))])

(deftemplate activity-template [state i {:keys [name] :as activity}]
  (let [lifts (map #(get-in state [:lifts %]) (:lifts activity))]
    [:div.activity {:data-name name}
     [:h3 name]
     (map lift-template lifts)
     [:a.button.new-lift-body {:data-activity-index i} "New Lift"]]))

(deftemplate day-template [{:keys [date activities] :as state}]
  [:div.day {:data-date date}
   [:h1 date]
   (map-indexed (partial activity-template state) activities)
   [:input.lift-type {:type "text" :placeholder "Lift Name"}]
   [:a.button.new-activity "New Activity"]])

(defn render-templates [state]
  (when (:refresh? state)
    (dommy/replace-contents! (sel1 :div#content) (day-template state))))

