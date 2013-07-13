(ns uplift.db-test
  (:require [clojure.test :refer :all]
            [clj-time.core :refer [now]]
            [uplift.db :refer :all]))

(def ^:dynamic *conn*)

(defmacro with-connection [uri & body]
  `(binding [*conn* (connect! uri)]
     (try ~@body
       (finally
         (disconnect! *conn*)))))
