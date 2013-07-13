(ns uplift.core
  (:require [uplift.views.index :as index]
            [uplift.views.login :as login]
            [uplift.views.signup :as signup]
            [uplift.views.add :as add]
            [uplift.session]
            [uplift.user]
            [uplift.workout]
            [compojure.core :refer [GET PUT POST DELETE ANY routes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.session :as session]
            [ring.middleware.params :as params]
            [ring.util.response :as response]
            [clj-time.core :refer [now]]
            [ring.server.standalone :refer [serve]]))

(defn redirect [res url]
  (-> res
    (assoc-in [:headers "Location"] url)
    (assoc :status 302)))

(defn redirect-as [user url]
  (redirect {:session {:session/user user}} url))

(defn create-handler* [db-conn]
  (routes
    (resources "/public")
    (GET "/" {user :user} (index/get-page {:user user}))
    (GET "/signup" [] (signup/get-page nil))
    (POST "/signup" [email password]
      (let [[errors user] (uplift.user/signup @db-conn email password)]
        (if errors
          (signup/get-page {:form {:email email
                                   :errors errors}})
          (redirect-as user "/"))))
    (GET "/login" [] (login/get-page nil))
    (POST "/login" [email password]
      (let [[err user] (uplift.user/login @db-conn email password)]
        (if err
          (login/get-page {:form {:email email
                                  :errors [err]}})
          (redirect-as user "/"))))
    (GET "/add" {user :user}
      (when user (add/get-page {:user user
                                :activites (uplift.workout/user-workout
                                             @db-conn
                                             user
                                             (now))})))
    (GET "/see" {user :user}
      (when user "see your workouts here"))
    (GET "/settings" {user :user}
      (when user "change your settings here"))
    (GET "/logout" [] (redirect-as nil "/"))
    (not-found "404")))

(defn wrap-user [handler]
  (fn [req]
    (let [user (get-in req [:session :session/user])]
      (handler (assoc req :user user)))))

(defn create-handler [db-conn]
  (-> (create-handler* db-conn)
    (params/wrap-params)
    (wrap-user)
    (session/wrap-session {:store (uplift.session/store db-conn)})
    (cookies/wrap-cookies)))
