(ns uplift.storage.protocol
  (:import java.util.concurrent.TimeUnit
           java.util.concurrent.Executors)
  (:require [clojure.java.io :refer [writer reader]]
            [clj-bcrypt-wrapper.core :refer [encrypt check-password]]
            [ring.middleware.session.store :refer [SessionStore]]
            [uplift.utils :as utils]))

(defprotocol Storage
  (init! [this config] "Performs any side effects necessary to initialize the
                       storage medium. Returns a function that reverses these
                       side effects and closes the storage medium.")
  (add-user [this username password])
  (get-user [this username])
  (get-user-by-id [this id])
  (check-pw [this username password])
  (change-password [this user new-password])

  (add-workout [this user workout])
  (get-workouts [this user])

  (read-session' [this key])
  (write-session' [this key data])
  (delete-session' [this key]))

(defn memory-blank-db []
  {:next-id 0
   :sessions {}
   :users {}})

(defn memory-empty-user [id username password]
  {:id id
   :username username
   :password (encrypt password)
   :workouts {}})

(defrecord MemoryStorage [db]
  Storage
  (init! [_ {:keys [path to-disk?]}]
    (let [persist-db (fn [] (when to-disk? (spit path (str @db))))
          read-db (fn [] (read-string (slurp path)))]
      (if to-disk?
        (try
          (reset! db (read-db))
          (catch java.io.IOException ioe
            (println "Database" path "not found, using test data")
            (reset! db (memory-blank-db))))
        (reset! db (memory-blank-db)))
      (let [shutdown-thread (Thread. persist-db)
            shutdown-hook (..
                            Runtime
                            getRuntime
                            (addShutdownHook shutdown-thread))
            thread-pool (Executors/newScheduledThreadPool 1)
            scheduled-exec (.
                            thread-pool
                            (scheduleAtFixedRate persist-db
                                                 (long 1)
                                                 (long 1)
                                                 (. TimeUnit MINUTES)))]
        #(do (.shutdown thread-pool)
             (.. Runtime getRuntime (removeShutdownHook shutdown-thread))
             (persist-db)))))

  (add-user [_ username password]
    (let [id (:next-id @db)
          user (memory-empty-user id username password)]
      (swap! db (fn [db]
                  (-> db
                    (assoc :next-id (inc id))
                    (assoc-in [:users id] user))))
      user))

  (get-user [_ username]
    (->> (map val (:users @db))
      (filter #(= (:username %) username))
      (first)))

  (get-user-by-id [_ id]
    (get-in @db [:users id]))

  (check-pw [this {old-pw :password} password]
    (check-password password old-pw))

  (change-password [_ {id :id} password]
    (swap! db (fn [db]
                (assoc-in db [:users id :password] (encrypt password)))))

  (add-workout [_ user workout]
    (swap! db (fn [db]
                (let [id (:next-id db)
                      workout (assoc workout :id id)]
                  (-> db
                    (assoc :next-id (inc id))
                    (assoc-in [:users (:id user) :workouts id] workout)))))
    (get-in @db [:users (:id user) :workouts]))

  (get-workouts [_ user]
    (vals (get-in @db [:users (:id user) :workouts])))

  (read-session' [_ key]
    (get-in @db [:sessions key]))

  (write-session' [_ key data]
    (let [key (or key (utils/rand-str 30))]
      (swap! db assoc-in [:sessions key] data)
      key))

  (delete-session' [_ key]
    (swap! db dissoc :sessions key)))

(deftype OurSession [store]
  SessionStore
  (read-session [_ key] (read-session' @store key))
  (write-session [_ key data] (write-session' @store key data))
  (delete-session [_ key] (delete-session' @store key)))

(defn session-store [s]
  (new OurSession s))
