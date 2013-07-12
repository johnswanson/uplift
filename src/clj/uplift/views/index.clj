(ns uplift.views.index
  (:require [uplift.views.base :as base]
            [hiccup.core]
            [hiccup.page :refer [html5 include-css include-js]]))

(def index-content
  [:div.row
   [:div.small-6.small-offset-3 "This is our sweet content!"]])

(defn get-page [{:keys [user]}]
  (base/base {:content index-content
              :top-links (base/links-for user)
              :current "/"}))
