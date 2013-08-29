(ns uplift.core
  (:require [uplift.js-utils :as utils]
            [uplift.views.add]
            [uplift.edn-ajax :as ajax]
            [domina.css :refer [sel]]
            [domina.events :refer [listen!]]
            [dommy.core :as dommy])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]
                   [dommy.macros :refer [sel sel1]]))

(def activities (atom nil))

(add-watch activities :log (fn [_ _ _ ns]
                             (utils/log (str ns))))

(defn add-new-activity! [name date]
  (swap! activities (fn [a]
                      (assoc a date {:name name
                                     :date date
                                     :lifts []}))))

(add-new-activity! "squat" "2012-08-2013")

(defn add-new-workout! [{date :date :as workout}]
  (swap! activities
         (fn [{{lifts :lifts} date :as activities} workout]
           (assoc-in activities [date :lifts] (conj lifts workout)))
         workout))

(defn lift-type-input [] (sel1 :input.lift-type))

(defn append-new-activity []
  (dommy/append!
    (sel1 :div#activities)
    (uplift.views.add/activity-template (dommy/value (lift-type-input)))))

(defn new-lift-body []
  (dommy/append! (sel1 :div#activities) (uplift.views.add/lift-body)))

(defn save-workout [workout]
  (let [c (ajax/request {:url "/add"
                     :method :post
                     :data (str workout)
                     :headers {:content-type "application/edn"}})]
    (go
      (let [[status resp] (<! c)]
        (case status
          :ok (add-new-workout! resp)
          :error (utils/log "error occurred:" (str resp)))))))

(listen! (sel "a.new-lift") :click (fn [evt] (save-workout {:date "2012-08-14"
                                                            :type "squat"
                                                            :reps 5})))
