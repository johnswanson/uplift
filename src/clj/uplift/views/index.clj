(ns uplift.views.index
  (:require [net.cgrand.enlive-html :as html]
            [uplift.views.base :as base]))

(def index-content (html/html-resource "uplift/views/index.html"))

(defn index [context]
  (apply
    str
    (html/emit* (base/content index-content))))
