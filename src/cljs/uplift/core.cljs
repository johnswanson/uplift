(ns uplift.core
  (:require [cljs.core.async :refer [chan put! <!]]
            [cljs.reader :as reader]
            [goog.net.XhrIo :as xhrio]
            [domina.css :refer [sel]]
            [domina.events :refer [listen!]])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]))

(defn edn-response [e]
  (let [res (.-target e)]
    (if (.isSuccess res)
      [:ok (reader/read-string (.getResponseText res))]
      [:error (.getStatus res)])))

(defn edn-ajax
  [{:keys [url method data headers]}]
  (let [method (clojure.string/upper-case (name method))
        c (chan)]
    (goog.net.XhrIo/send url
                         (fn [e] (put! c (edn-response e)))
                         method
                         data
                         (clj->js (merge {"Accept" "application/edn"}
                                         headers)))
    c))

(defn log [& data]
  (dorun (map #(.log js/console %) data)))

(defn test-fn []
  (let [c (edn-ajax {:url "/add"
                     :method :post
                     :data (str {:type "squat"
                                 :weight "145"
                                 :sets "3"
                                 :reps "5"
                                 :date "2013-08-21"})
                     :headers {:content-type "application/edn"}})]
    (go
      (let [[status value] (<! c)]
        (case status
          :ok (log (clj->js value))
          :error (log "error!"))))))

(listen! (sel "a.new-lift") :click (fn [evt] (test-fn)))
