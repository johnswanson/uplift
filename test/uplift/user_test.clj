(ns uplift.user-test
  (:require [uplift.user :refer :all]
            [uplift.db :refer [connect! disconnect!]]
            [clojure.test :refer :all]))

(def uri "datomic:free://localhost:4334/uplift")

(def ^:dynamic *conn*)

(defmacro with-connection [uri & body]
  `(binding [*conn* (connect! uri)]
     (try ~@body
       (finally
         (disconnect! *conn*)))))

(def username "username")
(def password "password")

(deftest users
  (with-connection uri
    (testing "I can add a new user and login with his username and password"
      (add-user *conn* username password)
      (let [user (get-user *conn* {:user/username username})]
        (is (= (:user/username user) username))
        (is (= user (login *conn* username password)))))))
