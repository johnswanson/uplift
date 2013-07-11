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

(deftest test-connection
  (with-connection uri
    (testing "Can add a workout"
      (create-workout *conn* (now))
      (is (= (day (now)) (:workout/day (get-workout *conn* (day (now)))))))))
