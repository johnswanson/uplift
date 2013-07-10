(ns uplift.system
  (:require [uplift.core]
            [datomic.api :as d]
            [uplift.db]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn system []
  (let [db (uplift.db/setup-db)
        handler (uplift.core/create-handler (:conn db))]
    {:db db
     :server {:handler handler}}))

(defn start!
  "Performs side effects to initialize the system, aquire resources, and start
  it running. Returns an updated instance of the system."
  [system]

  (let [get-atom (partial get-in system)
        server (run-jetty (get-in system [:server :handler])
                          {:port 8080 :join? false})
        conn (uplift.db/connect! (get-in system [:db :uri]))]
    (reset! (get-atom [:db :conn]) conn)
    (assoc-in system [:server :server] server)))

(defn stop!
  "Performs side effects to shut down the system and release its resources.
  Returns an updated instance of the system."
  [system]

  (.stop (get-in system [:server :server]))
  (d/release @(get-in system [:db :conn]))
  (reset! (get-in system [:db :conn]) nil)
  (assoc-in system [:server :server] nil))
