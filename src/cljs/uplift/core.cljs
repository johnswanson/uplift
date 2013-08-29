(ns uplift.core
  (:require [uplift.js-utils :as utils]
            [uplift.views.add]
            [uplift.edn-ajax :as ajax]
            [cljs.core.async :refer [put! chan <!]]
            [domina.css :refer [sel]]
            [domina.events :refer [listen!]]
            [dommy.core :as dommy])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]
                   [dommy.macros :refer [sel sel1]]))

(defn day-struct [date]
  {:date date
   :activities []})

(defn activity-struct [name]
  {:name name
   :lifts []})

(defn lift-struct []
  {:weight nil
   :reps nil
   :sets nil})

(defn save-workout [workout]
  (let [c (ajax/request {:url "/add"
                     :method :post
                     :data (str workout)
                     :headers {:content-type "application/edn"}})]
    (go
      (let [[status resp] (<! c)]
        (case status
          :ok (utils/log "successful: " (str resp))
          :error (utils/log "error occurred:" (str resp)))))))

(defn show-new-date [date]
  (dommy/append!
    (sel1 "div#activities")
    (uplift.views.add/day-template date)))

(defn day-context [date] (str "div.date-" date))

(defn add-new-activity [day]
  (let [name (dommy/value (sel1 [(day-context (:date day))
                                 :input.lift-type]))
        nd (update-in day [:activities] conj (activity-struct name))]
    (utils/log (str nd))
    nd))

(defn new-day-channels [day]
  (let [c (chan)
        ctxt (day-context (:date day))]
    (listen! (sel1 [ctxt :a.new-lift]) :click (fn [_] (put! c :new-activity)))
    c))

(defn new-date [date]
  (show-new-date date)
  (let [day (day-struct date)
        c (new-day-channels day)]
    (go
      (loop [day day]
        (let [evt (<! c)]
          (case evt
            :new-activity (recur (add-new-activity day))))))
    day))

(new-date "2012-08-29")

(listen! (sel "a.new-lift") :click (fn [evt] nil))

