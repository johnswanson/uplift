(ns uplift.views.base
  (:require [hiccup.core]
            [hiccup.page :refer [html5 include-css include-js]]))

(defn make-top-link [current {:keys [href text]}]
  (if (= current href)
    [:li.active [:a {:href href} text]]
    [:li [:a {:href href} text]]))

(defn links-for [user]
  (if user
    [{:href "/add" :text "Add Workouts"}
     {:href "/see" :text "See Workouts"}
     {:href "/settings" :text "Settings"}
     {:href "/logout" :text "Logout"}]
    [{:href "/login" :text "Login"}
     {:href "/signup" :text "Signup"}]))

(def base-scripts ["/public/js/uplift.js"])

(defn base [{:keys [content scripts top-links current]}]
  (html5 [:html {:lang "en"}
          [:head
           [:meta {:charset "UTF-8"}]
           [:title "Uplift"]
           [:link {:href "/public/css/app.css"
                   :rel "stylesheet"
                   :type "text/css"}]
           [:link {:href "/public/css/print.css"
                   :media "print"
                   :rel "stylesheet"
                   :type "text/css"}]
           [:link {:href (str "//netdna.bootstrapcdn.com/"
                              "font-awesome/3.2.1/css/font-awesome.css")
                   :rel "stylesheet"}]]
          [:body
           [:div#outer-wrapper 
            [:div#header
             [:nav.top-bar.fixed
              [:ul.title-area
               [:li.name [:h1 [:a {:href "/"} [:i.icon-upload] " Uplift"]]]]
              [:section.top-bar-section
               [:ul.left
                (map (partial make-top-link current) top-links)]]]]
            [:div#content content]]
           [:div#footer "footer content"]
           (include-js "/public/js/uplift.js")
           (apply include-js scripts)]]))
