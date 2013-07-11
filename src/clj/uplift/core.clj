(ns uplift.core
  (:require [uplift.views.index :refer [index]]
            [uplift.views.login :as login-view]
            [uplift.session]
            [compojure.core :refer [GET PUT POST DELETE ANY routes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.session :as session]
            [ring.middleware.params :as params]
            [ring.util.response :as response]
            [ring.server.standalone :refer [serve]]))

(defn redirect [res url]
  (-> res
    (assoc-in [:headers "Location"] url)
    (assoc :status 302)))

(defn create-handler* [db-conn]
  (routes
    (resources "/public")
    (GET "/" [] (index {:message "howdy hey"}))
    (GET "/login" [] (login-view/login nil))))

(defn create-handler [db-conn]
  (-> (create-handler* db-conn)
    (params/wrap-params)
    (session/wrap-session {:store (uplift.session/store db-conn)})
    (cookies/wrap-cookies)))
