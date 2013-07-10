(ns uplift.system
  (:require [uplift.core]
            [datomic.api :as d]
            [uplift.db]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn system []
  (let [db (uplift.db/setup-db)
        handler (uplift.core/create-handler db)]
    {:db db
     :handler handler}))

(defn start!
  "Performs side effects to initialize the system, aquire resources, and start
  it running. Returns an updated instance of the system."
  [system]
  (let [server (run-jetty (:handler system) {:port 8080 :join? false})
        conn (d/connect (get-in system [:db :uri]))]
    (conj system
          {:server server
           :conn conn})))

(defn stop!
  "Performs side effects to shut down the system and release its resources.
  Returns an updated instance of the system."
  [system]
  (.stop (:server system))
  (d/release (:conn system))
  (conj system
        {:server nil
         :conn nil}))
