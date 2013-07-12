(ns uplift.views.login
  (:require [uplift.views.base :as base]
            [hiccup.form :refer :all]
            [hiccup.core]
            [hiccup.page :refer [html5 include-css include-js]]))

(def login-content
  [:div.row
   [:div.small-6.small-offset-3
    (form-to [:post ""]
     (label "email" "Email Address:")
     (email-field {:placeholder "email@example.com" :autofocus true} "email")
     (label "password" "Password: ")
     (password-field {:placeholder "secret password"} "password")
     (submit-button "Login"))]])

(defn get-page [{:keys [user]}]
  (base/base {:content login-content
              :top-links (base/links-for user)
              :current "/login"}))
