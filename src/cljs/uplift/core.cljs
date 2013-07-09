(ns uplift.core)

(defn strkey 
  "Helper fn that converts keywords into strings"
  [x]
  (if (keyword? x)
    (name x)
    x))

(extend-type object
  ILookup
  (-lookup
    ([o k]
       (aget o (strkey k)))
    ([o k not-found]
       (let [s (strkey k)]
         (if (goog.object.containsKey o s)
           (aget o s)
           not-found)))))

(def test-data
  [["2013-01-01" [165 5 3]]
   ["2013-01-02" [155 5 3]]
   ["2013-01-04" [185 5 3]]
   ["2013-01-05" [195 5 3]]])

(defn log [& data]
  (dorun (map #(.log js/console %) data)))

(set! (.-onload js/window) (fn [] (log "loaded!")))
