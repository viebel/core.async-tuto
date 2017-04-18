(ns tuto.transducers
  (:require [criterium.core :refer [bench with-progress-reporting]]))


;; partition vs partition-all
(partition 4 (range 10))
(partition-all 4 (range 10))

;; transducers basics

(def my-inc (map inc))

(into [] my-inc (range 4))
(sequence my-inc (range 4))

;; composing

(def inc-and-odd (comp (filter odd?) (map inc)))

(time (last (sequence inc-and-odd (range 1e6))))

(with-progress-reporting (bench (->> (range 100)
                                     (filter odd?)
                                     (map inc)
                                     (into [])))) 

(with-progress-reporting (bench (into [] (comp
                                          (filter odd?)
                                          (map inc))
                                      (range 100))))


;; cat

(sequence (comp cat (partition-all 3)) [[0 1] [2] [3 4 5 6] [7 8] [9]])


;; transduce

(transduce (comp cat (partition-all 3)) concat [[0 1] [2] [3 4 5 6] [7 8] [9]])

;; it's the same as (reduce...)

(reduce concat (sequence (comp cat (partition-all 3)) [[0 1] [2] [3 4 5 6] [7 8] [9]]))

;; Write your own transducer

;; https://clojure.org/reference/transducers
;; stateful transducers
;; http://exupero.org/hazard/post/signal-processing/




