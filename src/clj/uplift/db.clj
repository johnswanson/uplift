(ns uplift.db
  (:use [uplift.config])
  (:require [korma.core :refer [defentity database]]
            [korma.db :refer [defdb mysql]]))

(defmacro select-one [& stuff]
  `(first (korma.core/select ~@stuff)))

(defdb -mysql (mysql (config [:mysql-login])))

(defentity session (database -mysql))
