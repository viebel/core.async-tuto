(ns tuto.chan
  (:require [clojure.core.async :refer [put! <!! >!! chan thread alts!! take!]]))


;; channel with no buffer
(def c (chan))
(def f (future (>!! c "a")
                (println "done")))

;; the thread is blocked until we read from the channel
f
(<!! c)


;; put! writes asynchronously - never blocks (until...)
(let [c (chan)]
  (put! c "hello")
  (<!! c))


;; put! has an internal buffer for storing the messages in case the channel buffer is empty

(let [c (chan)]
  (dotimes [i 1025]
    (put! c "hello")))

(let [c (chan 1)]
  (dotimes [i 1025]
    (put! c "hello")))


;; read messages with take!

(let [c (chan 1)]
  (take! c #(print (str "taken: " %)))
  (put! c "hi"))
