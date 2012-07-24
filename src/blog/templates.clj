(ns ^{:doc "doc-string"
      :author "Mayank Jain <mayank@helpshift.com>"}
  blog.templates
  (:require [blog.backend :as backend])
  (:use hiccup.element)
  (:use hiccup.form)
  (:use hiccup.page)                  ; For Doctype
  (:use hiccup.core))

(defn divs [classes & Body]
  (if (next classes)
    [:div {:class (first classes)} (divs (rest classes) Body)]
    [:div {:class (first classes)} Body]))

(defn gen-list [coll]
  (for [x coll]
    [:li [:a {:href (str "/tag/" x)} x]]))


(defn gen-post-link-anchor [time title]
  [:a {:href (backend/gen-post-link time title)} title])

(defn add-pagination [page-number]
  [:ul.pager
   (if (and (not= page-number (backend/get-max-pages)) (> (backend/get-max-pages) 1))
     [:li.next
      (link-to (str "/page-number/" (inc page-number)) "Older &rarr;")])
   (if (> page-number 1)
     [:li.previous
      (link-to (str "/page-number/" (dec page-number)) "&larr; Newer")])])

(defn view-body-index [& content]
  (list (divs ["navbar navbar-fixed-top" "navbar-inner" "container-fluid"]
              (link-to {:class "brand"} "/" "Firesofmay")
              (divs ["nav-collapse"]
                    (unordered-list {:class "nav"}
                                    [[:a {:href "/new-post"} [:i {:class "icon-plus icon-white"}]  "New Post"]]) ))
        (divs ["container-fluid" "row-fluid"]
              (divs ["span3" "well sidebar-nav"]
                    [:ul.nav.nav-list
                     (gen-list (backend/get-tags-db))]) ;if nothing in blog this blows up
              (divs ["span9"]
                    [:div.hero-unit
                     [:h1 "Blog of Firesofmay"]
                     [:p "This is my blog written in clojure."]]
                    (divs ["row-fluid"]
                          content)))))



(defn template-new-post []
  (form-to {:class "form-horizontal"} [:post "/new-post"]
    [:fieldset

     [:div.control-group
      (label {:class "control-label"} "title" "Title")
      [:div.controls
       [:input#title.input-xlarge {:name "title" :type "text" :style "width: 100%; padding: 4px 0px;"}]]]

     [:div.control-group
      (label {:class "control-label"} "tags" "Tags")
      [:div.controls
       [:input#tags.input-xlarge {:name "tags" :type "text" :style "width: 100%; padding: 4px 0px;"}]]]

     [:div.control-group
      (label {:class "control-label"} "post" "Post")
      [:div.controls
       [:textarea#post.input-xlarge {:name "post" :rows "10" :style "width: 100%; padding: 4px 0px;"}]]]

     [:div.form-actions
      [:button.btn.btn-primary {:type "Submit" :value "submit"} "Submit Post"]
      [:button.btn {:type "Reset" :value "Reset"} "Reset"]]]))

(defn add-comments [link]
  (form-to {:class "form-horizontal"} [:post link]
    [:fieldset
     [:legend "Enter Comments"]
     [:div.control-group
      (label {:class "control-label"} "username" "Username")
      [:div.controls
       [:input#username.input-xlarge
        {:name "username" :type "text"}]]]

     [:div.control-group
      (label {:class "control-label"} "comment" "Comment")
      [:div.controls
       [:textarea#comment.input-xlarge
        {:name "comment" :type "text"}]]]

     (divs ["control-group" "controls"]
           [:input {:type "hidden" :name "timestamp" :value (backend/get-link-id link)
                    }])

     (divs ["control-group" "controls"]
           [:input {:type "hidden" :name "link" :value link}])

     [:div.form-actions
      [:button.btn.btn-primary {:type "Submit" :value "submit"} "Add Comments"]
      [:button.btn {:type "Reset" :value "Reset"} "Reset"]]]))

(defn show-comments [coll]
  (list
   [:legend "Comments"]
   (for [x coll]
     (divs ["control-group" "controls"]
           [:h3 (:username x) " commented on : " (backend/year-month-date(:time x))]
           [:p (:comment x)]))))

(defn show-post [timestamp link]
  (let [post (backend/get-post timestamp)]
    (list [:h2 (:title post)]
          [:p (:post post)]
          (show-comments (backend/get-comments (str timestamp)))
          (add-comments link))))



(defn show-one-post [link uri]
  (show-post (backend/get-link-id link) uri))

(defn main-page [page]
  (list
   (let [coll (backend/pagination page)]
     (if (empty? coll)
       [:h2 "Write your first blog post by clicking on New Post."]
       (for [x coll]
         [:div.row-fluid
          [:h1 (gen-post-link-anchor (:time x) (:title x))]
          [:p (:post x)]])))
   (add-pagination page)))

(defn tag-page [coll]
  (let [coll (backend/get-coll-tag db-blog coll)]
    (if (empty? coll)
      [:h2 "Invalid Tag."]
      (for [x coll]
        [:div.row-fluid
         [:h1 (gen-post-link-anchor (:time x) (:title x))]
         [:p (:post x)]]))))

(defn view-head [& content]
  (html
   (doctype :xhtml-strict)
   (xhtml-tag "en"
              [:head
               [:meta {:charset "utf-8"}]
               [:title "My Blog"]
               [:meta {:name "viewport"
                       :content= "width=device-width, initial-scale=1.0"}]
               (include-css "/assets/css/bootstrap.css"
                            "/assets/css/bootstrap-responsive.css"
                            "/assets/css/inline.css")]
              [:body (view-body-index content)])))
