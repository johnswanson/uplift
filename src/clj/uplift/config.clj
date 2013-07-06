(ns uplift.config)

(defn config [v]
  (get-in (read-string (slurp "config.clj")) v))
