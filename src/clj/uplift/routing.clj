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
            [clj-time.core :refer [today local-date]]))

(defn redirect [res url]
  (-> res
    (assoc-in [:headers "Location"] url)
    (assoc :status 302)))

(defn redirect-as [user url]
  (if user
    (redirect {:session {:session/user-id (:id user)}} url)
    (redirect {:session {}} url)))

(defn signup [store params]
  (let [[status resp] (user/signup store params)]
    (case status
      :success (redirect-as resp "/")
      :failure (signup/get-page {:form {:email (:email params)
                                        :errors resp}}))))

(defn login [store params]
  (let [[status resp] (user/login store params)]
    (case status
      :success (redirect-as resp "/")
      :failure (login/get-page {:form {:email (:email params)
                                       :errors resp}}))))

(defn add-workout [store user params]
  (let [result (user/add-workout store user params)]
    (str result)))

(defn update-workout [store user params]
  (let [result (user/update-workout store user params)]
    (str result)))

(defn create-handler* [store]
  (routes
    (resources "/public")
    (GET "/" [] index/get-page)
    (GET "/signup" [] signup/get-page)
    (GET "/login" [] login/get-page)
    (POST "/signup" {params :params} (signup store params))
    (POST "/login" {params :params} (login store params))
    (GET "/see" {:keys [user params]}
      (when user (user/workouts store user params)))
    (GET "/add" {user :user} (when user add/get-page))
    (POST "/add" {:keys [user params]}
      (when user (add-workout store user params)))
    (GET "/logout" [] (redirect-as nil "/"))
    (not-found "404")))

(defn wrap-user [handler store]
  (fn [req]
    (let [user (user/by-id store (get-in req [:session :session/user-id]))]
      (handler (assoc req :user user)))))

(defn wrap-param-keywords [handler]
  (fn [{params :params :as req}]
    (-> (assoc req :params (into {} (map (fn [[k v]] [(keyword k) v]) params)))
      (handler))))

(defn create-handler [store]
  (-> (create-handler* store)
    (wrap-param-keywords)
    (params/wrap-params)
    (edn-middleware/wrap-edn-params)
    (wrap-user store)
    (session/wrap-session {:store (storage/session-store store)})
    (cookies/wrap-cookies)))
