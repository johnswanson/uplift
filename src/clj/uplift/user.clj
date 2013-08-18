(ns uplift.user
  (:require [uplift.storage.protocol :as storage]
            [red-tape.core :refer [form]]
            [slingshot.slingshot :refer [throw+]]
            [red-tape.cleaners :refer [non-blank]]))

(defn signup [store params]
  (let [non-existent (fn [data]
                       (if (storage/get-user @store (:email data))
                         (throw+ "A user exists with that email address")
                         data))
        signup (fn [data]
                 (-> data
                   (assoc :user
                          (storage/add-user @store
                                            (:email data)
                                            (:password data)))))
        form (form
               {}
               :email [non-blank]
               :password [non-blank]
               :red-tape/form [non-existent signup])]
    (form params)))

(defn login [store params]
  (let [check-pass (fn [data]
                     (let [user (storage/get-user @store (:email data))]
                       (if (and user (storage/check-pw @store
                                                       user
                                                       (:password data)))
                         (assoc data :user user)
                         (throw+ "No such user"))))
        form (form
               {}
               :email [non-blank]
               :password [non-blank]
               :red-tape/form check-pass)]
    (form params)))

(defn by-id [store id]
  (storage/get-user-by-id @store id))

(defn add-workout [store user {:keys [type date weight reps sets] :as workout}]
  (if (and type date weight reps sets)
    (do
      (storage/add-workout @store user workout)
      {:result true
       :errors []})
    {:result nil
     :errors ["Invalid workout info"]}))

(defn workouts [store user]
  (storage/get-workouts @store user))
