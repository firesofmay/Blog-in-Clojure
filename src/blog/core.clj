(ns blog.core
  (:use compojure.core)
  (:require compojure.route)
  (:use ring.util.response)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [blog.templates :as templates])
  (:require [blog.backend :as backend])
  (:use [ring.middleware.params :only [wrap-params]])
  (:use [ring.middleware.resource :only [wrap-resource]]))

(defroutes handler
  (GET "/" []
    (templates/view-head (templates/test-hello)))

  (GET "/new-post" []
    (templates/view-head (templates/template-new-post)))

  (compojure.route/not-found "Page not Found"))

(def app
  (-> #'handler
      wrap-params
      (wrap-resource "/")))


(defn start []
  (run-jetty #'app {:port 8080 :join? false}))
