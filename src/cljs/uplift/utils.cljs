(ns uplift.js-utils)

(defn log [& data]
  (dorun (map #(.log js/console %) data)))

