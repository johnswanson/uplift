(ns uplift.session
  (:use [datomic.api :only [q db] :as d])
  (:require [ring.middleware.session.store :refer [SessionStore]]))

(declare read-data save-data delete-data generate-new-random-key rand-char)

(deftype DatomicStore [conn]
  SessionStore
  (read-session [_ key]
    (read-data @conn key))
  (write-session [_ key data]
    (let [key (or key (generate-new-random-key))]
      (save-data @conn key data)
      key))
  (delete-session [_ key]
    (delete-data @conn key)
    nil))

(defn store [conn] (new DatomicStore conn))

(defn query-one [conn query] (d/entity
                               (db conn)
                               (ffirst (q query (db conn)))))

(defn get-session [conn session-id]
  (when session-id
    (query-one conn [:find '?e :where ['?e :session/session-id session-id]])))

(defn read-data [conn key]
  (get-session conn key))

(defn save-data [conn key data]
  (let [id-map (into {} (for [[k v] data] [k (:db/id v)]))]
    (d/transact conn [(conj {:db/id (d/tempid :workouts)
                             :session/session-id key} id-map)])))

(def cs (map char (concat (range 48 58) (range 66 92) (range 97 123))))
(defn rand-char [] (nth cs (.nextInt (java.util.Random.) (count cs))))

(defn generate-new-random-key []
  (apply str (take 64 (repeatedly rand-char))))

(defn delete-data [conn key]
  (let [session (get-session conn key)]
    (d/transact conn [[:db.fn/retractEntity (:db/id session)]])))

(defn delete-user-session [conn user]
  (let [sess-id (ffirst (q [:find '?sess-id
                            :where ['?sess-id] ['?sess-id :session/user (:db/id user)]]
                           (db conn)))]
    (d/transact conn [[:db.fn/retractEntity sess-id]])))
