(ns ^{:doc "doc-string"
      :author "Mayank Jain <mayank@helpshift.com>"}
  blog.backend
  (:require [clj-time.core :as time])
  (:use monger.operators)
  (:require monger.joda-time) ;this is to serialize the time when we store the timestamp
  (:require monger.json)      ;this is required by joda-time
  (:require [clj-time.format :as tf])
  (:require [monger.core :as mg])
  (:refer-clojure :exclude [sort find])
  (:use monger.query)
  (:require [hiccup.util :as hu])
  (:require [monger.collection :as mc]))

;; localhost, default port
(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))

(def ^:const db-blog "blogs")
(def ^:const db-comments "comments")
(def ^:const db-base "base-coll")
(def ^:const max-page 5)

(defn year-month [time]
  (tf/unparse (tf/formatter "yyyy/MM") time))

(defn year-month-date [time]
  (tf/unparse (tf/formatter "YYYY/MM/dd") time))

(year-month-date  (first (map #(:time %) (mc/find-maps db-comments))))

(defn get-coll-tag [tag]
  (mc/find-maps db-blog
                {:tags {$in [tag]}}))

(defn get-coll [coll]
  (mc/find-maps coll))

(defn get-tags-db []
  (let [coll (mc/find-maps db-base)]
    (if (empty? coll)
      [""]
      (apply :tags coll))))

(defn show [coll]
  (clojure.pprint/pprint
   (mc/find-maps coll)))

(defn add-tags [coll tags]
  (doseq [tag tags]
    (mc/update coll {:_id 1} {$addToSet {:tags tag}} :upsert true)))

(defn time-format [timestamp]
  (tf/unparse (tf/formatters :year-month-day) timestamp))


(defn sub-newlines [body]
  (clojure.string/replace body #"\r\n" "<br>") )

(defn sanitize-title [title]
  (str "/" (clojure.string/join "-" (map #(.toLowerCase %) (re-seq #"[A-Za-z0-9]+" title)))))

(defn getepoch [timestamp]
  (int (/ (.getMillis timestamp) 1000)))

(defn gen-post-link [time title]
  (str "/" (year-month time) (sanitize-title title) "-" (getepoch time)))

(defn get-link-id [link]
  (Integer/parseInt (last (re-seq #"\d+" link))))

(defn get-post [timestamp]
  (mc/find-one-as-map db-blog {:timestamp timestamp}))


(defn sanitize-tags [tags]
  (map #(clojure.string/replace % #"[ \t\n]+" "-")
       (map clojure.string/trim
            (clojure.string/split (hu/escape-html (.toLowerCase tags)) #","))))

(defn insert-comments-into-db [username comment timestamp]
  (mc/insert db-comments {:timestamp timestamp
                          :time (time/now)
                          :username (hu/escape-html username)
                          :comment (hu/escape-html (sub-newlines comment))}))

(defn get-comments [timestamp]
  (mc/find-maps db-comments {:timestamp timestamp}))

(defn insert-post-into-db [title tags post]
  (let [sanitized-tags (sanitize-tags tags)]
    (mc/insert db-blog {:timestamp (getepoch (time/now))
                        :time (time/now)
                        :title (hu/escape-html title)
                        :tags sanitized-tags
                        :post (sub-newlines (hu/escape-html post))})
    (add-tags db-base sanitized-tags)))

(defn pagination [page-number]
  (with-collection db-blog
    (find {})
    (fields [:title :post :time])
    (sort {:time -1})
    (paginate :page page-number :per-page max-page)))

(defn get-max-pages []
  (long (Math/ceil (/ (mc/count db-blog) max-page))))



(comment
  (do
    (mc/remove db-blog)
    (mc/remove db-base)
    (mc/remove db-comments)))

(comment
  (doseq [x [["1" "tag1, tag2" "111"]
             ["2" "tag1" "111"]
             ["3" "tag2" "111"]
             ["4" "tag2" "111"]
             ["5" "tag1,tag3" "111"]
             ["6" "tag1,tag3" "111"]
             ["7" "tag1,tag3" "111"]
             ["8" "tag1,tag3" "111"]
             ["9" "tag1,tag4" "111"]
             ["10" "tag1,tag4" "111"]
             ["11" "tag1,tag4" "111"]
             ["12" "tag1,tag4" "111"]
             ["13" "tag1,tag4" "111"]]]
    (apply insert-post-into-db x)))
