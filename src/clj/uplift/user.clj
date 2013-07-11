(ns uplift.user
  (:use [datomic.api :only [q db] :as d])
  (:require [clj-bcrypt-wrapper.core :refer [encrypt check-password]]))

(declare get-user)

(defn add-user [conn username password]
  (d/transact conn [{:db/id (d/tempid :workouts)
                     :user/username username
                     :user/password (encrypt password)}]))

(defn mod-user!
  "Modifies a user using a map"
  [conn user m]
  (d/transact conn [(conj {:db/id (:db/id user)} m)]))

(defn rem-user!
  "Removes data from a user"
  [conn user key value]
  (d/transact conn [{:db/retract (:db/id user) key value}]))

(defn change-password! [conn user new-pass]
  (mod-user! conn user {:user/password (encrypt new-pass)}))

(defn add-cell [conn user cell]
  (mod-user! conn user {:user/cell-number cell}))

(defn login
  "Attempts to find a user with a given username and password. If successful,
  returns that user. If unsuccessful, returns nil."
  [conn username password]
  (when-let [user (get-user conn {:user/username username})]
    (when-let [pass (:user/password user)]
      (when (check-password password pass) user))))

(defn get-user
  "Attempts to find a user using a map of things true about that user"
  [conn m]
  (d/entity (db conn)
            (ffirst (q {:find ['?e]
                        :where (map
                                 (fn [kvp] ['?e (key kvp) (val kvp)])
                                 m)}
                       (db conn)))))
