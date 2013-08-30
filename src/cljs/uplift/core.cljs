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

(def days (atom []))

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

(defn new-day [date]
  {:selector (format "[data-date='%s']" date)
   :date date
   :activities []
   :type :day})

(defn new-activity [name]
  {:selector (format "[data-activity='%s']" name)
   :name name
   :lifts []
   :type :activity})

(defn sel-in-context [{type :type :as thing} & sels]
  (sel (into [] (concat [(:selector thing)] sels))))

(defn sel1-in-context [{type :type :as thing} & sels]
  (sel1 (into [] (concat [(:selector thing)] sels))))

(defn render-day "Renders a day and returns a channel used to transmit user
                 actions about that day."
  [day]
  (dommy/append! (sel1 :div#activities) (uplift.views.add/day-template day))
  (let [c (chan)
        new-lift-name #(dommy/value (sel1-in-context day :input.lift-type))
        new-lift-fn #(put! c [:new-lift (new-lift-name)])]
    (listen! (sel1-in-context day :a.new-lift) :click new-lift-fn)
    (listen! (sel1-in-context day :input.lift-type) :keydown
             #(if (= (:keyCode %) 13) (new-lift-fn %)))
    c))

(defn render-activity "Renders an activity and returns a channel used to
                      transmit user interactions with that activity"
  [day activity]
  (dommy/append! (sel1 :div#activities)
                 (uplift.views.add/activity-template activity)))

(let [day (new-day "2012-08-29")
      c (render-day day)]
  (go
    (loop []
      (let [[status resp] (<! c)]
        (case status
          :new-lift (when (not (empty? resp))
                      (render-activity day (new-activity resp)))
          (js/log "invalid status:" (str status) (str resp)))
        (recur)))))

(listen! (sel "a.new-lift") :click (fn [evt] nil))

