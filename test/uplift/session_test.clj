(ns uplift.session-test
  (:require [uplift.session :refer :all]
            [uplift.db :refer [connect! disconnect!]]
            [uplift.user]
            [clojure.test :refer :all]))

(def uri "datomic:free://localhost:4334/uplift")

(def ^:dynamic *conn*)

(defmacro with-connection [uri & body]
  `(binding [*conn* (connect! uri)]
     (try ~@body
       (finally
         (disconnect! *conn*)))))


(deftest test-session
  (with-connection uri
    (delete-user-session *conn* (uplift.user/get-user *conn* {:user/username
                                                              "username"}))
    (testing "Testing sessions"
      (let [key (generate-new-random-key)
            user (or
                   (uplift.user/get-user *conn* {:user/username "username"})
                   (do (uplift.user/add-user *conn* "username" "password")
                       (uplift.user/get-user *conn* {:user/username "username"})))]
        (do
          (delete-user-session *conn* user)
          (save-data *conn* key {:session/user user})
          (is (= (get-in (read-data *conn* key) [:session/user :user/username]) "username"))
          (delete-data *conn* key)
          (is (= nil (read-data *conn* key))))))))
