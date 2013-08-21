(ns uplift.user
  (:require [uplift.storage.protocol :as storage]
            [uplift.utils :as utils]
            [red-tape.core :refer [form]]
            [slingshot.slingshot :refer [throw+]]
            [red-tape.cleaners :refer [non-blank]]
            [clj-time.core :as clj-time]))

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

(defn add-workout [store user params]
  (let [add-workout (partial storage/add-workout @store user)
        form (form
               {}
               :type [non-blank]
               :date [#(if (re-matches #"\d{4}-\d{2}-\d{2}" %)
                         %
                         (throw+ "Invalid format"))]
               :weight [non-blank]
               :reps [non-blank]
               :sets [non-blank]
               :red-tape/form [add-workout])]
    (form params)))

(defn workouts
  ([store user] (workouts store user {}))
  ([store user {:keys [date type start-date end-date]}]
   (filter (fn [{w-date :date w-type :type}]
             (and (if date (= w-date date) true)
                  (if type (= w-type type) true)
                  (if start-date (clj-time/after? (utils/date w-date)
                                                  (utils/date start-date)) true)
                  (if end-date (clj-time/before? (utils/date w-date)
                                                 (utils/date end-date)) true)))
           (storage/get-workouts @store user))))

