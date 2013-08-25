(ns uplift.user
  (:require [uplift.storage.protocol :as storage]
            [uplift.utils :as utils]
            [red-tape.core :refer [form]]
            [red-tape.cleaners :refer [non-blank]]
            [slingshot.slingshot :refer [throw+]]
            [clj-time.core :as clj-time]))

(defn check [requirements values]
  (apply merge-with concat
         (for [[keyname functions] requirements
               [f response] (partition 2 functions)
               :let [value (keyname values)]]
           (if (f value) nil {keyname response}))))

(defn signup [store {:keys [email password] :as params}]
  (let [requirements {:email [(complement empty?)
                              "You must enter an email address"
                              (complement (partial storage/get-user @store))
                              "The email address you entered is already
                              associated with an account."]
                      :password [(complement empty?)
                                 "You must enter a password"]}
        errors (check requirements params)]
    (if (empty? errors)
      [:success (storage/add-user @store email password)]
      [:failure errors])))

(defn login [store {:as params' :keys [email password]}]
  (let [params (assoc params' :both [email password])
        requirements {:email [(complement empty?)
                              "You must enter an email address"]
                      :password [(complement empty?)
                                 "You must enter a password"]
                      :both [(fn [[e p]]
                               (when-let [user (storage/get-user @store e)]
                                 (storage/check-pw @store user p)))
                             "Incorrect email or password"]}
        errors (check requirements params)]
    (if (empty? errors)
      [:success (storage/get-user @store email)]
      [:failure errors])))

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

