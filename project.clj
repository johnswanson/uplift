(defproject uplift "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler uplift.core/app
         :port 8080
         :auto-refresh? true}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring-server "0.2.8"]
                 [compojure "1.1.5"]])
