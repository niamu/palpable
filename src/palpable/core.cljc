(ns palpable.core
  (:require [clojure.string :as string]
            [hickory.core :as h]
            [hickory.select :as s]
            [palpable.config :refer [config]]))

(defn customer-id
  [page]
  (-> (s/descendant (s/id "myLibraryForm")
                    (s/and (s/tag :input)
                           (s/attr :name #(= % "downloadCustId"))))
      (s/select page)
      first :attrs :value))

(defn books
  [page]
  (-> (s/descendant (s/class "adbl-lib-content")
                    (s/tag "table")
                    (s/has-child (s/and (s/tag "td")
                                        (s/attr :name #(= % "productCover")))))
      (s/select page)))

(defn input-value
  [f attr]
  (-> (s/descendant (s/and (s/tag "input")
                           (s/attr :name #(= % attr))))
      (s/select f)
      first :attrs :value))

(defn book-url
  [book customer-id]
  (str "http://cds.audible.com/download?"
       "user_id=" customer-id
       "&product_id=" (input-value book "productId")
       "&order_number=" (input-value book "ordNumber")
       "&cust_id=" customer-id
       "&codec=" (input-value book "selectFormat")
       "&awtype=AAX"))

(defn book-author
  [book]
  (->  (s/descendant (s/class "adbl-library-item-author")
                     (s/tag "a"))
       (s/select book)
       first :content first))

(defn book-title
  [book]
  (-> (s/descendant (s/class "adbl-library-item-title")
                    (s/tag "a"))
      (s/select book)
      first :content first))

(defn -main
  [& args]
  (let [page (-> (slurp "resources/library.html") h/parse h/as-hickory)]
    (spit "script/download.sh"
          "#!/bin/sh\n\n")
    (doall
     (map (fn [book]
            (let [source-dir (str "\"resources/sources/"
                                  (book-author book) "/\"")
                  clean-dir (str "\"resources/clean/"
                                 (book-author book) "/\"")]
              (spit "script/download.sh"
                    (str "mkdir -p " source-dir ";\n"
                         "mkdir -p " clean-dir ";\n"
                         "curl \"" (book-url book (customer-id page)) "\""
                         " -o " (str source-dir
                                     "\"" (book-title book) "\"" ".aax;\n")
                         "ffmpeg -i "
                         (str source-dir "\"" (book-title book) "\"" ".aax") " "
                         (str source-dir "\"" (book-title book) "\"" ".png")
                         ";\n"
                         "ffmpeg -activation_bytes "
                         (:activation-bytes config)
                         " -i "
                         (str source-dir "\"" (book-title book) "\"" ".aax")
                         " -c:a copy -vn -f mp4 "
                         (str clean-dir "\"" (book-title book) "\"" ".m4b")
                         ";\n"
                         "mp4art --add "
                         (str source-dir "\"" (book-title book) "\"" ".png") " "
                         (str clean-dir "\"" (book-title book) "\"" ".m4b;"
                              "\n\n"))
                    :append true)))
          (books page)))))
