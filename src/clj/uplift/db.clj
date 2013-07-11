(ns uplift.db
  (:use [datomic.api :only [q db] :as d])
  (:require [clj-time.core :as clj-time]
            [clj-time.coerce :as coerce]
            [clj-time.format]
            [clj-bcrypt-wrapper.core :refer [encrypt check-password]]))

(defn setup-db
  "Used to set up the DB in system.clj"
  ([] {:uri "datomic:free://localhost:4334/uplift"
       :conn (atom nil)}))

(declare pprint-workout pprint-lift)

(def uri "datomic:free://localhost:4334/uplift")

(defn schema-tx [] (read-string (slurp "schema.dtm")))
(defn connect!
  "Creates the database (if it doesn't already exist), connects to it, and
  returns that connection."
  [uri]
  (d/create-database uri)
  (let [conn (d/connect uri)]
    (d/transact conn (schema-tx))
    conn))

(defn disconnect! [conn] (d/release conn))

(defn day
  ([] (coerce/to-long (clj-time/today-at 00 00)))
  ([t] (coerce/to-long
         (clj-time/date-time (clj-time/year t)
                             (clj-time/month t)
                             (clj-time/day t)))))

(defn get-workout [conn day]
  (d/entity (db conn) (ffirst (q [:find '?e
                                  :where ['?e :workout/day day]] (db conn)))))

(defn add-lift [conn workout {:keys [weight reps sets type]}]
  (d/transact conn [{:db/id (d/tempid :workouts)
                     :lift/weight weight
                     :lift/reps reps
                     :lift/sets sets
                     :lift/type type
                     :workout/_lifts (:db/id workout)}]))

(defn get-workouts
  ([conn] ; get ALL workouts
   (map #(d/entity (db conn) (first %))
        (q [:find '?e :where ['?e :workout/day]] (db conn)))))

(defn create-workout [conn time]
  (d/transact conn [{:db/id (d/tempid :workouts)
                     :workout/day (day time)}])
  (get-workout conn (day time)))

(defn add-user [conn username password]
  (d/transact conn [{:db/id (d/tempid :workouts)
                     :user/username username
                     :user/password (encrypt password)}]))

(def date-format (clj-time.format/formatter "yyyy-MM-dd"))

(defn pprint-workout [workout]
  (str "Workout (" (clj-time.format/unparse
                     date-format
                     (coerce/from-long (:workout/day workout)))
       ")\n"
       (clojure.string/join ", " (map pprint-lift (:workout/lifts workout)))))
       
(defn pprint-lift [lift]
  (str (:lift/weight lift) "x"
       (:lift/reps lift) "x"
       (:lift/sets lift) " (" (:lift/type lift) ")\n"))
