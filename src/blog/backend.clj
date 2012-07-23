(ns ^{:doc "doc-string"
      :author "Mayank Jain <mayank@helpshift.com>"}
  blog.backend
  (:require [clj-time.core :as time])
  (:use monger.operators)
  (:require monger.joda-time) ;this is to serialize the time when we store the timestamp
  (:require monger.json)      ;this is required by joda-time
  (:require [clj-time.format :as tf])
  (:require [monger.core :as mg])
  (:require [monger.collection :as mc]))

;; localhost, default port
(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))

(def ^:const db-blog "blogs")
(def ^:const db-comments "comments")
(def ^:const db-base "base-coll")

(defn gen-post-link [timestamp title]
  [:a {:href (str "/" (year-month timestamp) (sanitize-title title) "-" (getepoch timestamp))} title])

(defn get-posts-db []
  (map #(list (gen-post-link (:time %) (:title %) ))
                            (mc/find-maps db-blog)))


(defn get-tags-db []
                (apply :tags (mc/find-maps db-base)))

(defn show [coll]
  (clojure.pprint/pprint
   (mc/find-maps coll)))

(defn add-tags [coll tags]
  (doseq [tag tags]
    (mc/update coll {:_id 1} {$addToSet {:tags (clojure.string/lower-case tag)}} :upsert true)))

(defn time-format [timestamp]
  (tf/unparse (tf/formatters :year-month-day) timestamp))

(defn year-month [timestamp]
  (tf/unparse (tf/formatter "yyyy/MM") timestamp))


(defn sub-newlines [body]
                 (clojure.string/replace body #"\r\n" "<br>") )

(defn sanitize-title [title]
  (str "/" (clojure.string/join "-" (map #(.toLowerCase %) (re-seq #"[A-Za-z0-9]+" title)))))

(defn getepoch [timestamp]
  (int (/ (.getMillis timestamp) 1000)))


(defn sanitize-tags [tags]
  (map #(clojure.string/replace % #"[ \t\n]+" "-")
                   (map clojure.string/trim
                        (clojure.string/split (.toLowerCase tags) #","))))

(defn insert-post-into-db [title tags post]
  (mc/insert db-blog {:timestamp (getepoch (time/now))  :time (time/now) :title title :tags (sanitize-tags tags) :post (sub-newlines post)})
  (add-tags db-base (sanitize-tags tags)))
