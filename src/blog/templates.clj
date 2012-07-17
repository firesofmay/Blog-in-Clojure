(ns ^{:doc "doc-string"
      :author "Mayank Jain <mayank@helpshift.com>"}
  blog.templates
  (:use hiccup.element)
  (:use hiccup.page)                  ; For Doctype
  (:use hiccup.core))

(defn divs [classes & Body]
             (if (next classes)
               [:div {:class (first classes)} (divs (rest classes) Body)]
               [:div {:class (first classes)} Body]))


(defn view-body-index []
  (list (divs ["navbar navbar-fixed-top" "navbar-inner" "container-fluid"]
         (link-to {:class "brand"} "/" "Firesofmay"))
   (divs ["container-fluid" "row-fluid" "span3" "well sidebar-nav"] "Hello")))



(defn view-head [& content]
  (html
   (doctype :xhtml-strict)
   (xhtml-tag "en"
              [:head
               [:meta {:charset "utf-8"}]
               [:title "My Blaaag"]
               [:meta {:name "viewport"
                       :content= "width=device-width, initial-scale=1.0"}]
               (include-css "/assets/css/bootstrap.css"
                            "/assets/css/bootstrap-responsive.css"
                            "/assets/css/inline.css")
               ]
              [:body (view-body-index)])))
