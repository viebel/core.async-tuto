(ns tuto.buffer
  (:require [clojure.core.async :refer [put! <!! >!! chan dropping-buffer buffer sliding-buffer thread]]))


(defn write-and-read []
    ;; channel with buffer
    (let [c (chan 10)]
      (>!! c "hello")
      (<!! c)))

(comment
  (write-and-read))


(defn write-on-dropping-buffer []
;;; channel with dropping-buffer

  (let [c (chan (dropping-buffer 5))]
    (dotimes [i 10]
      (>!! c i))
    (<!! c)
    c))

(comment
  (def c (write-on-dropping-buffer))
  (<!! c))

(defn write-on-sliding-buffer []
;;; channel with sliding-buffer
  (let [c (chan (sliding-buffer 5))]
    (dotimes [i 10]
      (>!! c i))
    (<!! c)))

(comment (write-on-sliding-buffer))


(defn write-with-no-buffer []
  (let  [c (chan)]
    (thread (>!! c "a");; the thread is blocked until we read from the channel
            (println "done"))
    c))

(comment
  (def c (write-with-no-buffer))
  (<!! c))


