(ns uplift.routing
  (:require [uplift.views.index :as index]
            [uplift.views.login :as login]
            [uplift.views.signup :as signup]
            [uplift.views.add :as add]
            [uplift.storage.protocol :as storage]
            [uplift.user :as user]
            [compojure.core :refer [GET PUT POST DELETE ANY routes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.session :as session]
            [ring.middleware.params :as params]
            [ring.middleware.edn :as edn-middleware]
            [ring.util.response :as response]
            [clj-time.core :refer [now]]))

(defn redirect [res url]
  (-> res
    (assoc-in [:headers "Location"] url)
    (assoc :status 302)))

(defn redirect-as [user url]
  (if user
    (redirect {:session {:session/user-id (:id user)}} url)
    (redirect {:session {}} url)))

(defn signup [store email password]
  (let [{:keys [result errors]} (user/signup store email password)]
    (if result
      (redirect-as result "/")
      (signup/get-page {:form {:email email
                               :errors errors}}))))

(defn login [store email password]
  (let [{:keys [result errors]} (user/login store email password)]
    (if result
      (redirect-as result "/")
      (login/get-page {:form {:email email
                              :errors errors}}))))

(defn create-handler* [store]
  (routes
    (resources "/public")
    (GET "/" [] index/get-page)
    (GET "/signup" [] signup/get-page)
    (GET "/login" [] login/get-page)
    (POST "/signup" [email password] (signup store email password))
    (POST "/login" [email password] (login store email password))
    (GET "/see" {user :user} (user/workouts store user))
    (POST "/add" {:keys [user params]}
      (user/add-workout store user params))
    (GET "/logout" [] (redirect-as nil "/"))
    (not-found "404")))

(defn wrap-user [handler store]
  (fn [req]
    (let [user (user/by-id store (get-in req [:session :session/user-id]))]
      (handler (assoc req :user user)))))

(defn create-handler [store]
  (-> (create-handler* store)
    (params/wrap-params)
    (edn-middleware/wrap-edn-params)
    (wrap-user store)
    (session/wrap-session {:store (storage/session-store store)})
    (cookies/wrap-cookies)))
