(ns uplift.js-utils
  (:require [cljs.core.async :refer [put! chan <!]]
            [dommy.core :as dommy :refer [listen!]]
            [domina.events]
            [domina.css])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]
                   [dommy.macros :refer [sel sel1]]))

(declare merge-chans filter-chan click-chan)

(defn log [& data]
  (dorun (map #(.log js/console %) data)))

(defn merge-chans [& chans]
  (let [rc (chan)]
    (go (loop []
          (put! rc (first (alts! chans)))
          (recur)))
    rc))

(defn filter-chan [pred channel]
  (let [rc (chan)]
    (go (loop []
          (let [val (<! channel)]
            (if (pred val) (put! rc val))
            (recur))))
    rc))

(defn ui-chan [type msg-name selectors]
  (let [filter-fn (fn [e] (#{(.-target e)} (sel selectors)))
        rc (filter-chan filter-fn (chan))]
    (listen! (concat [(sel1 :body)] selectors)
             type
             (fn [e]
               (.preventDefault e)
               (put! rc [msg-name e])))
    rc))

(defn click-chan [msg-name & selectors]
  (ui-chan :click msg-name selectors))

(defn blur-chan [msg-name & selectors]
  (ui-chan :focusout msg-name selectors))
