(ns ^{:doc "doc-string"
      :author "Mayank Jain <mayank@helpshift.com>"}
  blog.backend
  (:require [clj-time.core :as time])
  (:require monger.joda-time) ;this is to serialize the time when we store the timestamp
  (:require monger.json)      ;this is required by joda-time
  (:require [clj-time.format :as tf])
  (:require [monger.core :as mg])
  (:require [monger.collection :as mc]))
