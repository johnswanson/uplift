(ns uplift.checker
  (:require [uplift.storage.protocol :as storage]
            [uplift.utils :as utils]
            [plumbing.core :refer :all]
            [plumbing.graph :as graph]))

(defnk email [params] (:email params))
(defnk email-present? [email] ((comp not empty?) email))
(defnk password [params] (:password params))
(defnk pass-present? [password] ((comp not empty?) password))
(defnk password [params] (:password params))
(defnk user [store email email-present?]
  (when email-present? (storage/get-user @store email)))
(defnk pass-correct? [store user password pass-present?]
  (when (and user pass-present?) (storage/check-pw @store user password)))
(defnk login-errors [email-present? pass-present? pass-correct?]
  (remove nil? [(if-not email-present? "You must enter an email address.")
                (if-not pass-present? "You must enter a password.")
                (if-not pass-correct? "Incorrect email address or password.")]))
(defnk signup-errors [email-present? pass-present? user]
  (remove nil? [(if-not email-present? "You must enter an email address.")
                (if-not pass-present? "You must enter a password.")
                (if user "Someone has already signed up with that email.")]))
(defnk valid [errors] (empty? errors))

(def login-requirements
  (graph/lazy-compile {:email-present? email-present?
                       :email email
                       :pass-present? pass-present?
                       :password password
                       :user user
                       :pass-correct? pass-correct?
                       :errors login-errors
                       :valid valid}))

(def signup-requirements
  (graph/lazy-compile {:email-present? email-present?
                       :email email
                       :pass-present? pass-present?
                       :password password
                       :user user
                       :errors signup-errors
                       :valid valid}))

(defn check-login [store params]
  (login-requirements {:store store :params params}))

(defn check-signup [store params]
  (signup-requirements {:store store :params params}))

(defnk id [params] (try (utils/parse-int (:id params))
                        (catch Exception e  nil)))
(defnk workout-type [params] (:type params))
(defnk weight [params] (try (utils/parse-int (:weight params))
                            (catch Exception e nil)))
(defnk reps [params] (try (utils/parse-int (:reps params))
                          (catch Exception e nil)))
(defnk sets [params] (try (utils/parse-int (:sets params))
                          (catch Exception e nil)))
(defnk id-present? [id] (not (nil? id)))
(defnk new-workout-errors [id-present?]
  (remove nil? [(if id-present? "ID present in new workout")]))
(defnk update-workout-errors [id-present?]
  (remove nil? [(if-not id-present? "ID required to update workout")]))

(def new-workout-requirements
  (graph/lazy-compile {:id id
                       :type workout-type
                       :weight weight
                       :reps reps
                       :sets sets
                       :id-present? id-present?
                       :errors new-workout-errors
                       :valid valid}))

(def update-workout-requirements
  (graph/lazy-compile {:id id
                       :type workout-type
                       :weight weight
                       :reps reps
                       :sets sets
                       :id-present? id-present?
                       :errors update-workout-errors
                       :valid valid}))

(defn check-new-workout [params]
  (new-workout-requirements {:params params}))

(defn check-update-workout [params]
  (update-workout-requirements {:params params}))
