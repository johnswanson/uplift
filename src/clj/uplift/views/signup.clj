(ns uplift.views.signup
  (:use [plumbing.core])
  (:require [uplift.views.base :as base]
            [hiccup.form :refer :all]
            [hiccup.core]
            [hiccup.page :refer [html5 include-css include-js]]))

(defn signup-content [{:keys [email errors]}]
  [:div.row
   [:div.small-6.small-offset-3.columns
    [:fieldset
     (form-to [:post ""]
      (map (fn [err] [:span.error err]) errors)
      (label "email" "Email Address:")
      (email-field
        {:placeholder "email@example.com" :autofocus true}
        "email" email)
      (label "password" "Password: ")
      (password-field {:placeholder "secret password"} "password")
      (submit-button {:class "signup"} "Sign Up!"))]]])

(defn get-page [{:keys [form user]}]
  (base/base {:content (signup-content form)
              :top-links (base/links-for user)
              :current "/signup"}))
