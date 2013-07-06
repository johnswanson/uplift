(ns uplift.session
  (:require [uplift.db :refer [session select-one]]
            [ring.middleware.session.store :refer [SessionStore]]
            [korma.core :as korma :refer [defentity select update insert values
                                          where delete set-fields exec-raw]]
            [clojure.tools.reader.edn :as edn]))

(declare read-data save-data delete-data generate-new-random-key rand-char)

(deftype MysqlStore []
  SessionStore
  (read-session [_ key]
    (read-data key))
  (write-session [_ key data]
    (let [key (or key (generate-new-random-key))]
      (save-data key data)
      key))
  (delete-session [_ key]
    (delete-data key)
    nil))

(defn store [] (new MysqlStore))

(defn read-data [key]
  (edn/read-string (:data
                     (select-one session (where {:ring-session key})))))

(defn save-data [key data]
  (exec-raw ["REPLACE INTO session (`ring-session`, `data`) VALUES (?, ?)"
             [key (str data)]]))

(def cs (map char (concat (range 48 58) (range 66 92) (range 97 123))))
(defn rand-char [] (nth cs (.nextInt (java.util.Random.) (count cs))))

(defn generate-new-random-key []
  (apply str (take 255 (repeatedly rand-char))))

(defn delete-data [key]
  (delete session (where {:ring-session key})))
