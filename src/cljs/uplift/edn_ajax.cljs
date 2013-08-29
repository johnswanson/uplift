(ns uplift.edn-ajax
  (:require [cljs.core.async :refer [chan put! <!]]
            [cljs.reader :as reader]
            [goog.net.XhrIo :as xhrio]))

(defn edn-response [e]
  (let [res (.-target e)]
    (if (.isSuccess res)
      [:ok (reader/read-string (.getResponseText res))]
      [:error (.getStatusText res)])))

(defn request
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

