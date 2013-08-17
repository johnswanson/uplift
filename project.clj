(defproject uplift "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :plugins [[lein-ring "0.8.5"]
            [lein-cljsbuild "0.3.2"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [ring-mock "0.1.5"]]}}
  :source-paths ["src/clj"]
  :cljsbuild {:builds [{:source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/uplift.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"] ; clojure itself
                 [org.clojure/tools.namespace "0.2.3"]

                 [prismatic/plumbing "0.1.0"]

                 [digest "1.3.0"]

                 [com.datomic/datomic-free "0.8.4020"]

                 [ring/ring-core "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [ring-server "0.2.8"] ; the server

                 [compojure "1.1.5"] ; routing

                 [org.clojure/tools.reader "0.7.4"] ; edn reader

                 [clj-time "0.5.1"] ; time

                 [clj-bcrypt-wrapper "0.1.0"]
                  

                 [hiccup "1.0.3"]
                 [hiccups "0.2.0"]
                 [org.clojure/google-closure-library-third-party "0.0-2029"]
                 [domina "1.0.2-SNAPSHOT"]

                 ])
