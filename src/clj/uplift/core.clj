(ns uplift.core
  (:require [uplift.views.index :refer [index]])
  (:require [compojure.core :refer [GET PUT POST DELETE ANY defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.params :as params]
            [ring.util.response :as response]
            [ring.server.standalone :refer [serve]]))

(defn redirect [res url]
  (-> res
    (assoc-in [:headers "Location"] url)
    (assoc :status 302)))

(defroutes routes
  (resources "/public")
  (GET "/" [] (index {:message "hey"})))

(def app (-> routes
           (params/wrap-params)
           (cookies/wrap-cookies)))

(defn -main
  ([] (-main 8080))
  ([port] (serve app {:port (Integer. port)
                      :open-browser? false
                      :stacktraces? false})))
