(ns uplift.core
  (:require [uplift.views.index :as index]
            [uplift.views.login :as login]
            [uplift.views.signup :as signup]
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
    (GET "/" [] (index/get-page nil))
    (GET "/signup" [] (signup/get-page nil))
    (GET "/login" [] (login/get-page nil))))

(defn create-handler [db-conn]
  (-> (create-handler* db-conn)
    (params/wrap-params)
    (session/wrap-session {:store (uplift.session/store db-conn)})
    (cookies/wrap-cookies)))
