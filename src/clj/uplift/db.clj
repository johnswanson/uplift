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
