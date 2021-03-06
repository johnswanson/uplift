(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [uplift.system :as system]
            [uplift.storage.protocol :as storage]
            [uplift.routing :as routing]
            [uplift.utils :as utils]
            [datomic.api :as d :refer [q db]]))

(def system nil)

(defn init
  "Constructs the current development system"
  []
  (alter-var-root #'system
                  (constantly (system/system))))

(defn start
  "Starts the current development system"
  []
  (alter-var-root #'system system/start!))

(defn stop
  "Shuts down and destroys the current development system"
  []
  (alter-var-root #'system (fn [s] (when s (system/stop! s)))))

(defn go
  "Initializes the current development system and starts it running"
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn store [] (get-in system [:storage :store]))
