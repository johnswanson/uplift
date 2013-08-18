(ns uplift.user
  (:require [uplift.storage.protocol :as storage]))

(defn signup [store email password]
  (if (storage/get-user @store email)
    {:result nil
     :errors ["A user with that username already exists."]}
    {:result (storage/add-user @store email password)
     :errors []}))

(defn login [store email password]
  (let [user (storage/get-user @store email)]
    (if (and user (storage/check-pw @store user password))
      {:result user
       :errors nil}
      {:result nil
       :errors ["The username or password provided was incorrect."]})))

(defn by-id [store id]
  (storage/get-user-by-id @store id))

(defn add-workout [store user workout]
  (storage/add-workout @store user workout))

(defn workouts [store user]
  (storage/get-workouts @store user))
