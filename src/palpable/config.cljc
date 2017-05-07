(ns palpable.config
  #?(:clj  (:require [clojure.edn :as edn])
     :cljs (:require-macros [fume.config :refer [config*]])))

#?(:clj
   (defmacro config*
     []
     (edn/read-string (slurp "./resources/config.edn"))))

(def config (config*))
