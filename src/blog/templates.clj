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
    [:li [:a {:href (str "/" x)} x]]))


(defn view-body-index [& content]
  (list (divs ["navbar navbar-fixed-top" "navbar-inner" "container-fluid"]
              (link-to {:class "brand"} "/" "Firesofmay")
              (divs ["nav-collapse"]
                    (unordered-list {:class "nav"}
                                    [[:a {:href "/new-post"} [:i {:class "icon-plus icon-white"}]  "New Post"]]) ))
        (divs ["container-fluid" "row-fluid"]
              (divs ["span3" "well sidebar-nav"]
                    [:ul.nav.nav-list
                     (gen-list (backend/get-tags-db))])
              (divs ["span9" "row-fluid" "span9"] content))))

(defn template-new-post []
  (form-to {:class "form-horizontal"} [:post "/new-post"]
    [:fieldset

     [:div.control-group
      (label {:class "control-label"} "input01" "Title")
      [:div.controls
       [:input#input01.input-xlarge {:name "title" :type "text" :style "width: 100%; padding: 4px 0px;"}]]]

     [:div.control-group
      (label {:class "control-label"} "input02" "Tags")
      [:div.controls
       [:input#input02.input-xlarge {:name "tags" :type "text" :style "width: 100%; padding: 4px 0px;"}]]]

     [:div.control-group
      (label {:class "control-label"} "text1" "Post")
      [:div.controls
       [:textarea#text1.input-xlarge {:name "post" :rows "10" :style "width: 100%; padding: 4px 0px;"}]]]

     [:div.form-actions
      [:button.btn.btn-primary {:type "Submit" :value "submit"} "Submit Post"]
      [:button.btn {:type "Reset" :value "Reset"} "Reset"]]]))

(defn test-hello []
  "Hello World")

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
                            "/assets/css/inline.css")
               ]
              [:body (view-body-index content)])))
