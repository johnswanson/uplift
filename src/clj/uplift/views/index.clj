(ns uplift.views.index
  (:require [net.cgrand.enlive-html :as html]))

(html/deftemplate index "uplift/views/index.html"
  [ctxt]
  [:head :title] (html/content (:message ctxt)))
