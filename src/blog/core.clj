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
    (templates/view-head (templates/main-page)))

  (GET "/new-post" []
    (templates/view-head (templates/template-new-post)))

  (POST "/new-post" [title tags post]
    (backend/insert-post-into-db title tags post)
    (redirect "/"))

  (GET ["/:year/:month/:link" :year #"\d{4}"  :month #"\d{2}"] []
    (fn [req]
      (templates/view-head (templates/show-one-post (get-in req [:route-params :link]) (:uri req)))))

  (POST ["/:year/:month/:link" :year #"\d{4}"  :month #"\d{2}"] [username comment timestamp]
    (backend/insert-comments-into-db username comment timestamp)
    (redirect "/"))

  (compojure.route/not-found "Page not Found"))

(def app
  (-> #'handler
      wrap-params
      (wrap-resource "/")))


(defn start []
  (run-jetty #'app {:port 8080 :join? false}))
