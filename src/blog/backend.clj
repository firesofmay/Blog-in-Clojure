(ns ^{:doc "doc-string"
      :author "Mayank Jain <mayank@helpshift.com>"}
  blog.backend
  (:require [clj-time.core :as time])
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

(defn gen-post-link [timestamp title]
  [:a {:href (str "/" (year-month timestamp) (sanitize-title title) "-" (getepoch timestamp))} title])


(defn get-posts-db []
  (map #(list (gen-post-link (:time %) (:post-title %) ))
                            (mc/find-maps db-blog)))



#_(defn fetch-db-blogs []
  (view-layout "<ul>"
               (map #(list "<li>"
                           (time-format (:time %)) "<br>"
                           (gen-post-link (:time %) (:post-title %) ) "<br>"
                           (:post-body %) "<br><br>"
                           "</li>")
                    (mc/find-maps db-blog))
               "</ul>"))


(defn insert-post-into-db [title body]
  (mc/insert db-blog {:timestamp (getepoch (time/now))  :time (time/now) :post-title title :post-body (sub-newlines body)}))

(defn get-link-id [link]
                  (Integer/parseInt (last (re-seq #"\d+" link))))

(defn get-post [timestamp]
  (mc/find-one-as-map db-blog {:timestamp timestamp}))


(defn get-comments [timestamp]
  (mc/find-one-as-map "comments" {:timestamp timestamp}))

(defn insert-comments-into-db [username comment timestamp]
  (mc/insert "comments" {:timestamp timestamp :time (time/now) :username username :comment comment}))
