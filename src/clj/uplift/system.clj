(ns uplift.system
  (:require [uplift.storage.protocol]
            [uplift.core]
            [ring.adapter.jetty :refer [run-jetty]])
  (:import [uplift.storage.protocol MemoryStorage]))

(defn initial-db []
  {:next-id 0
   :users {}})

(defn load-db []
  (if-let [data (try
                  (read-string (slurp "data/data.dtm"))
                  (catch Exception e nil))]
    data
    (initial-db)))

(defn system []
  (let [storage (atom nil)
        handler (uplift.core/create-handler storage)]
    {:storage {:config {:type :memory}
               :store storage}
     :server {:handler handler
              :server nil}}))

(defn start!
  "Performs side effects to initialize the system, aquire resources, and start
  it running. Returns an updated instance of the system."
  [system]
  (let [server (run-jetty (get-in system [:server :handler])
                          {:port 8080 :join? false})
        store (case (get-in system [:storage :config :type])
                :memory (new MemoryStorage (atom (load-db))))]
    (reset! (get-in system [:storage :store]) store)
    (assoc-in system [:server :server] server)))

(defn stop!
  "Performs side effects to shut down the system and release its resources.
  Returns an updated instance of the system."
  [system]

  (.stop (get-in system [:server :server]))
  (reset! (get-in system [:storage :store]) nil)
  (assoc-in system [:server :server] nil))
