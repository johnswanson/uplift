(ns uplift.views.base
  (:require [net.cgrand.enlive-html :as html]))

(def base (html/html-resource "uplift/views/base.html"))

(defn content
  "Takes some stuff and puts it into the main content div of our base template"
  [stuff]
  (html/transform base [:div#wrapper] (html/content stuff)))
