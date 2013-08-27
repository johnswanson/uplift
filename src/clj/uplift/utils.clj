(ns uplift.utils
  (:require [compojure.core]
            [clj-time.core :refer [local-date]]))

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

(def cs (map char (concat (range 48 58) (range 66 92) (range 97 123))))
(defn rand-char [] (nth cs (.nextInt (java.util.Random.) (count cs))))
(defn rand-str [n] (apply str (take n (repeatedly rand-char))))

(defn parse-int [s] (Integer. s))

(defn date [s]
  (->> s
    (re-matches #"(\d{4})-(\d{1,2})-(\d{1,2})")
    (next)
    (map parse-int)
    (#(if (seq %)
        (try (apply local-date %)
            (catch Exception e nil))
        nil))))

(defn remove-nil-values [m]
  (into {} (remove (fn [[k v]] (nil? v)) m)))

