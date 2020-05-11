(ns tuto.buffer
  (:require [clojure.core.async
             :refer
             [<!! >!! chan dropping-buffer sliding-buffer thread]]))

(defn write-and-read []
    ;; channel with buffer
  (let [c (chan 10)]
    (>!! c "hello")
    (<!! c)))

(comment
  (write-and-read))


;;; channel with dropping-buffer


(defn write-on-dropping-buffer []
  (let [c (chan (dropping-buffer 5))]
    (dotimes [i 10]
      (>!! c i))
    c))

(comment
  (def c (write-on-dropping-buffer))
  ;;; Only the first messages are kept
  ;;; We can read 5 times and then we block
  [(<!! c)
   (<!! c)
   (<!! c)
   (<!! c)
   (<!! c)])

;;; channel with sliding-buffer
(defn write-on-sliding-buffer []
  (let [c (chan (sliding-buffer 5))]
    (dotimes [i 10]
      (>!! c i))
    c))

(comment
  (def c (write-on-sliding-buffer))
  ;;; the first messages are dropped
  ;;; We can read 5 times and then we block
  [(<!! c)
   (<!! c)
   (<!! c)
   (<!! c)
   (<!! c)])

(defn write-with-no-buffer []
  (let [c (chan)]
    (thread (>!! c "a");; the thread is blocked until we read from the channel
            (println "done"))
    c))

(comment
  (def c (write-with-no-buffer))
  (<!! c))



;;; Exercise
;;; Write code that simulates a race between 5 threads
;;; Each thread writes its id on a channel. The fastest thread wins
(comment
  (str (Thread/currentThread)))
