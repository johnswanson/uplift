(ns uplift.views.login
  (:require [net.cgrand.enlive-html :as html]
            [uplift.views.base :as base]))

(def login-layout (html/html-resource "uplift/views/login.html"))

(defn login [context]
  (apply str (html/emit* (base/content login-layout))))
