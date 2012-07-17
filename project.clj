(defproject test-blog "0.1.0-SNAPSHOT"
  :description "This this a test Blog"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [ring/ring-core "1.1.0"]
                 [ring/ring-devel "1.1.0"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [compojure "1.1.0"]
                 [clj-time "0.4.3"]
                 [com.novemberain/monger "1.0.1"]
                 [org.clojure/data.json "0.1.2"] ;this is required by joda-time
                 [hiccup "1.0.0"]]
  :main test-blog.core
  :dev-dependencies
  [[lein-run "1.0.0-SNAPSHOT"]])
