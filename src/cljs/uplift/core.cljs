(ns uplift.core
  (:require [uplift.js-utils
             :as utils
             :refer [click-chan blur-chan filter-chan merge-chans]]
            [uplift.views.add]
            [uplift.edn-ajax :as ajax]
            [cljs.core.async :refer [put! chan <!]]
            [dommy.core :as dommy :refer [listen!]])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]
                   [dommy.macros :refer [sel sel1]]))

(declare add-new-lift add-new-activity no-refresh refresh change-lift lift-saved)

(def saved-lift-channel (chan))

(defn save-lift! [lift]
  (let [rc saved-lift-channel
        updating? (:on-server? lift)
        url (if updating? (format "/update/%d" (:id lift)) "/add")
        response-chan (ajax/request
                        {:url url
                         :method :post
                         :data (str lift)
                         :headers {:content-type "application/edn"}})]
    (go (let [[status resp] (<! response-chan)]
          (case status
            :ok (put! rc [:lift-saved (merge lift resp {:on-server? true})])
            :error (utils/log "error occurred!" (str resp)))))
    rc))

(def user-inputs (merge-chans
                   (click-chan :new-activity :a.new-activity)
                   (click-chan :new-lift :a.new-lift-body)
                   (blur-chan :change-weight :input.weight)
                   (blur-chan :change-sets :input.sets)
                   (blur-chan :change-reps :input.reps)
                   saved-lift-channel))

(defn data [elem key]
  (dommy/attr elem (str "data-" (name key))))

(defn apply-changed-input-to-lift [state input key]
  (change-lift state
               (js/parseInt (data input :local-id))
               key
               (dommy/value input)))

(defn app [start-state]
  (go
    (loop [state start-state]
      (utils/log (str state))
      (uplift.views.add/render-templates state)
      (let [[msg value] (<! user-inputs)
            target (try (.-target value) (catch js/Error e nil))]
        (-> (case msg
              :new-activity (refresh
                              (add-new-activity
                                state
                                (dommy/value (sel1 :input.lift-type))))
              :new-lift (refresh (add-new-lift state (data target :activity-index)))
              :change-weight (no-refresh (change-lift state
                                                      (js/parseInt (data target :local-id))
                                                      :weight
                                                      (dommy/value target)))
              :change-sets (no-refresh (change-lift state
                                                    (js/parseInt (data target :local-id))
                                                    :sets
                                                    (dommy/value target)))
              :change-reps (no-refresh (change-lift state
                                                    (js/parseInt (data target :local-id))
                                                    :reps
                                                    (dommy/value target)))
              :lift-saved (refresh (lift-saved state value))
              state)
          (recur))))))

(def initial-state {:next-id 0
                    :refresh? true
                    :date "2012-08-30"
                    :activities []
                    :lifts {}})

(app initial-state)

(defn lift-saved [state lift]
  (update-in state [:lifts] assoc (:local-id lift) lift))

(defn add-new-activity [state name]
  (-> state
    (update-in [:activities] conj {:name name, :lifts []})))

(defn add-new-lift [state activity-index]
  (let [id (:next-id state)]
    (-> state
      (update-in [:activities activity-index :lifts] conj id)
      (update-in [:lifts] assoc id {:sets nil, :reps nil,
                                    :weight nil, :editing? true,
                                    :local-id id})
      (update-in [:next-id] inc))))

(defn change-lift [state id key val]
  (let [new-state (update-in state [:lifts id] assoc key (js/parseInt val))
        new-lift (get-in new-state [:lifts id])]
    (save-lift! new-lift)
    new-state))

(defn refresh [state] (assoc state :refresh? true))
(defn no-refresh [state] (assoc state :refresh? false))

