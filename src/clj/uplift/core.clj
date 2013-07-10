(ns uplift.core
  (:require [uplift.views.index :refer [index]])
  (:require [compojure.core :refer [GET PUT POST DELETE ANY routes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.params :as params]
            [ring.util.response :as response]
            [ring.server.standalone :refer [serve]]))

(defn redirect [res url]
  (-> res
    (assoc-in [:headers "Location"] url)
    (assoc :status 302)))

(defn create-handler* [db]
  (routes
    (resources "/public")
    (GET "/" [] (index {:message "howdy hey"}))))

(defn create-handler [db]
  (-> (create-handler* db)
    (params/wrap-params)
    (cookies/wrap-cookies)))
