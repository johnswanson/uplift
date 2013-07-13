(ns uplift.user
  (:use [datomic.api :only [q db] :as d]
        [plumbing.core])
  (:require [clj-bcrypt-wrapper.core :refer [encrypt check-password]]
            [plumbing.graph :as graph]))

(declare get-user
         add-user
         username-graph
         password-graph
         validate-username
         validate-password)

(defn login
  "Attempts to find a user with a given username and password. If successful,
  returns that user. If unsuccessful, returns nil."
  [conn username password]
  (let [user (get-user conn {:user/username username})]
    (if (and user (check-password password (:user/password user)))
      [nil user]
      ["Invalid username or password" nil])))

(defn signup [conn username password]
  (let [u-valid (validate-username {:username username :conn conn})
        p-valid (validate-password {:password password})]
    (if (and (:valid u-valid)
             (:valid p-valid))
      (do (add-user conn username password)
          [nil (get-user conn {:user/username username})])
      [(concat (:error u-valid) (:error p-valid)) nil])))

(defn add-user [conn username password]
  (d/transact conn [{:db/id (d/tempid :workouts)
                     :user/username username
                     :user/password (encrypt password)}]))

(defn validate-username [conn username]
  (let [results (validate-username {:username username, :conn conn})]
    (if (:valid results)
      true
      (:error results))))

(defn validate-password [password]
  (let [results (validate-password {:password password})]
    (if (:valid results)
      true
      (:error results))))

(def valid-graph
  {:error (fnk [validate] (remove nil? validate))
   :valid (fnk [error] (not (seq error)))})

(def username-graph
  (merge valid-graph
         {:validate (fnk [conn canonical]
                      [(when (get-user conn {:user/username canonical})
                         "That email address is already in our database.")
                       (when (= (count canonical) 0)
                         "You must enter an email address!")])
          :canonical (fnk [username] (clojure.string/lower-case username))}))

(def password-graph
  (merge valid-graph
         {:validate (fnk [password]
                      [(when (= (count password) 0)
                         "You must enter a non-empty password!")])}))

(def validate-username (graph/lazy-compile username-graph))
(def validate-password (graph/lazy-compile password-graph))

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

(defn get-user
  "Attempts to find a user using a map of things true about that user"
  [conn m]
  (d/entity (db conn)
            (ffirst (q {:find ['?e]
                        :where (map
                                 (fn [kvp] ['?e (key kvp) (val kvp)])
                                 m)}
                       (db conn)))))
