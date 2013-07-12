(ns uplift.views.signup
  (:require [uplift.views.base :as base]
            [hiccup.form :refer :all]
            [hiccup.core]
            [hiccup.page :refer [html5 include-css include-js]]))

(def signup-content
  [:div.row
   [:div.small-6.small-offset-3
    (form-to [:post ""]
     (label "email" "Email Address:")
     (email-field {:placeholder "email@example.com" :autofocus true} "email")
     (label "password" "Password: ")
     (password-field {:placeholder "secret password"} "password")
     (submit-button "Sign Up!"))]])

(defn get-page [{:keys [user]}]
  (base/base {:content signup-content
              :top-links (base/links-for user)
              :current "/signup"}))
