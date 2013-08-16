(ns uplift.core
  (:require [uplift.views.index :as index]
            [uplift.views.login :as login]
            [uplift.views.signup :as signup]
            [uplift.views.add :as add]
            [uplift.storage.protocol :as storage]
            [compojure.core :refer [GET PUT POST DELETE ANY routes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.session :as session]
            [ring.middleware.params :as params]
            [ring.util.response :as response]
            [clj-time.core :refer [now]]))

(defn redirect [res url]
  (-> res
    (assoc-in [:headers "Location"] url)
    (assoc :status 302)))

(defn redirect-as [user url]
  (redirect {:session {:session/user user}} url))

(defn create-handler* [store]
  (routes
    (resources "/public")
    (GET "/" {user :user} (index/get-page {:user user}))
    (GET "/signup" [] (signup/get-page nil))
    (POST "/signup" [email password] (str (storage/add-user @store email password)))
    (GET "/login" [] (login/get-page nil))
    (not-found "404")))

(defn wrap-user [handler]
  (fn [req]
    (let [user (get-in req [:session :session/user])]
      (handler (assoc req :user user)))))

(defn create-handler [store]
  (-> (create-handler* store)
    (params/wrap-params)
    (wrap-user)
    (session/wrap-session)
    (cookies/wrap-cookies)))
