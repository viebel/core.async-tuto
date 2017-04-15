(ns tuto.basics
  (:require [clojure.core.async :refer [go go-loop <! <!! >!! >! chan put! dropping-buffer buffer sliding-buffer thread]]))



;; channel with buffer
(let [c (chan 10)]
  (>!! c "hello")
  (<!! c))

;;; channel with dropping-buffer
(let [c (chan (dropping-buffer 1))]
  (dotimes [i 10]
    (>!! c i))
  (<!! c))

;;; channel with sliding-buffer
(let [c (chan (sliding-buffer 1))]
  (dotimes [i 10]
    (>!! c i))
  (<!! c))

;; channel with no buffer
(def c (chan))
(thread (>!! c "a")
        (println "done"))

;; the thread is blocked until we read from the channel
(<!! c)

;; Go blocks
(def cc (chan))
(go (>! c "a")
    (println "done"))

(go (<! c))







