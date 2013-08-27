(ns uplift.user
  (:require [uplift.storage.protocol :as storage]
            [uplift.utils :as utils]
            [uplift.checker :as checker]
            [slingshot.slingshot :refer [throw+]]
            [clj-time.core :as clj-time]))

(defn signup [store {:as params :keys [email password]}]
  (let [check (checker/check-signup store params)]
    (if (:valid check)
      [:success (storage/add-user @store email password)]
      [:failure (:errors check)])))

(defn login [store {:as params :keys [email password]}]
  (let [check (checker/check-login store params)]
    (if (:valid check)
      [:success (:user check)]
      [:failure (:errors check)])))

(defn by-id [store id]
  (storage/get-user-by-id @store id))

(def workout-keys [:id :type :weight :reps :sets])

(defn add-workout [store user params]
  (let [check (checker/check-new-workout params)]
    (if (:valid check)
      [:success (storage/add-workout
                  @store
                  user
                  (utils/remove-nil-values (select-keys check workout-keys)))]
      [:failure (:errors check)])))

(defn update-workout [store user params]
  (let [check (checker/check-update-workout params)]
    (if (:valid check)
      [:success (storage/update-workout
                  @store
                  user
                  (utils/remove-nil-values (select-keys check workout-keys)))]
      [:failure (:errors check)])))

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

