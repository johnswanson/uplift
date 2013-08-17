(ns uplift.system
  (:require [uplift.storage.protocol :as storage]
            [uplift.routing]
            [ring.adapter.jetty :refer [run-jetty]])
  (:import [uplift.storage.protocol MemoryStorage]))

(defn default-config []
  {:storage {:type :memory
             :path "data/data.dtm"
             :to-disk? true}
   :server {:port 8080 :join? false}})

(defn config [& args]
  (let [data (try
               (read-string (slurp "config.clj"))
               (catch Exception e nil))]
    (-> (merge-with merge (default-config) data)
      (get-in args))))

(defn system []
  (let [storage (atom nil)
        handler (uplift.routing/create-handler storage)]
    {:storage {:store storage}
     :server {:handler handler
              :server nil}}))

(defn start!
  "Performs side effects to initialize the system, aquire resources, and start
  it running. Returns an updated instance of the system."
  [system]
  (let [server (run-jetty (get-in system [:server :handler])
                          (config :server))
        store (case (config :storage :type)
                :memory (new MemoryStorage (atom nil))
                nil)]
    (reset! (get-in system [:storage :store]) store)
    (-> system
      (assoc-in [:storage :shutdown]
                (storage/init! store (config :storage)))
      (assoc-in [:server :server] server))))

(defn stop!
  "Performs side effects to shut down the system and release its resources.
  Returns an updated instance of the system."
  [system]

  (.stop (get-in system [:server :server]))
  ((get-in system [:storage :shutdown]))
  (reset! (get-in system [:storage :store]) nil)
  (-> system
    (assoc-in [:server :server] nil)
    (assoc-in [:storage :shutdown] nil)))
