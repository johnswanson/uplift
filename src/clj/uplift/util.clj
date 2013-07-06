(ns uplift.util
  (:require [compojure.core]))

(defn wrap-check-user [handler u-type]
  (fn [{{r-type :type} :user :as req}]
    (if (or (= u-type (or r-type :none))
            (= u-type :any))
      (handler req)
      nil)))

(defmacro defroutes
  [rname & forms]
  (let [map-fn (fn [[method path u-type params res]]
                 (if-not u-type
                   `(~method ~path)
                   (let [handler (eval `(~method ~path ~params ~res))]
                     (wrap-check-user handler u-type))))]
    `(compojure.core/defroutes ~rname ~@(map map-fn forms))))
