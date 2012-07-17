(ns blog.core
    (:use compojure.core)
  (:require compojure.route)
  (:use hiccup.core)
  (:use [hiccup.page :only [doctype xhtml-tag]])                    ;for doctype
  (:use ring.util.response)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [clj-time.core :as time])
  (:require [blog.templates :as templates])
  (:require monger.joda-time) ;this is to serialize the time when we store the timestamp
  (:require monger.json)      ;this is required by joda-time
  (:require [clj-time.format :as tf])
  (:require [monger.core :as mg])
  (:require [monger.collection :as mc])
  (:use [ring.middleware.params :only [wrap-params]])
  (:use [ring.middleware.resource :only [wrap-resource]]))

(defroutes handler
  (GET "/" []
    (templates/view-head "Hello")))

(def app
  (-> #'handler
      wrap-params
      (wrap-resource "/")))


(defn start []
  (run-jetty #'app {:port 8080 :join? false}))
