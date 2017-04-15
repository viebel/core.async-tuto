(ns tuto.transducers)


;; partition vs partition-all
(partition 4 (range 10))
(partition-all 4 (range 10))

;; transducers basics

(def my-inc (map inc))

(into [] my-inc (range 4))
(sequence my-inc (range 4))

;; composing

(def inc-and-odd (comp (filter odd?) (map inc)))

(sequence inc-and-odd (range 5))

;; cat

(sequence (comp cat (partition-all 3)) [[0 1] [2] [3 4 5 6] [7 8] [9]])


;; transduce

(transduce (comp cat (partition-all 3)) concat [[0 1] [2] [3 4 5 6] [7 8] [9]])

;; it's the same as (reduce...)

(reduce concat (sequence (comp cat (partition-all 3)) [[0 1] [2] [3 4 5 6] [7 8] [9]]))


