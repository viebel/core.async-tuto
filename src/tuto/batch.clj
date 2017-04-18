(ns tuto.batch
  (:require [clojure.core.async :refer [pipeline go go-loop <! <!! >!! >! chan put! dropping-buffer buffer sliding-buffer thread]]))

;; batch with transducers
(defn batch-transduce [in max-size]
  (let [out (chan 1)
        xf (comp cat (partition-all max-size))]
    (pipeline 1 out xf in)
    out))

(let [c (chan 1)
      b (batch-transduce c 3)]
  (go-loop []
    (println "batch (transducers): " (<! b))
    (recur))
  (dotimes [i 10]
    (put! c (range i))))

;; batch with transducers - even better

(defn batch-xf [max-size]
  (comp cat (partition-all max-size)))

(let [b (chan 1 (batch-xf 3))]
  (go-loop []
    (println "batch (xf): " (<! b))
    (recur))
  (dotimes [i 15]
    (put! b (range i))))


;; Exercises
;; Design a system where messages are sent as sequences of arbitrary length
;; and processed in sequences of 3 items

