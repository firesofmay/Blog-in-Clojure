(ns ^{:doc "doc-string"
      :author "Mayank Jain <mayank@helpshift.com>"}
  blog.templates
  (:use hiccup.element)
  (:use hiccup.page)                  ; For Doctype
  (:use hiccup.core))

(defn div3
  [class1 class2 class3 & content]
  [:div {:class class1}
   [:div {:class class2}
    [:div {:class class3}
     content]]])

(defn view-body-index []
  (div3 "navbar navbar-fixed-top" "navbar-inner" "container-fluid"
        (link-to {:class "brand"} "/" "Firesofmay")
        [:div {:class "btn-group pull-right"}
         [:a {:class "btn dropdown-toggle"
              :data-toggle "dropdown"
              :href "#"}
          [:i {:class "carret"}]
          ]]))

(defn view-head [& content]
  (html
   (doctype :xhtml-strict)
   (xhtml-tag "en"
              [:head
               [:meta {:charset "utf-8"}]
               [:title "My Blaaag"]
               [:meta {:name "viewport"
                       :content= "width=device-width, initial-scale=1.0"}]
               (include-css "/assets/css/bootstrap.css" "/assets/css/bootstrap-responsive.css")
               ]
              [:body (view-body-index)])))
