(ns uplift.core
  (:require [compojure.core :refer [GET PUT POST DELETE ANY defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.cookies :as cookies]
            [ring.middleware.session :as session]
            [ring.middleware.params :as params]
            [ring.util.response :as response]
            [ring.server.standalone :refer [serve]]))

(defroutes routes
  (resources "/static")
  (GET "/" [] "hello world"))

(def app (-> routes
           (params/wrap-params)
           (cookies/wrap-cookies)))

(defn -main
  ([] (-main 8080))
  ([port] (serve app {:port (Integer. port)
                      :open-browser? false
                      :stacktraces? false})))
