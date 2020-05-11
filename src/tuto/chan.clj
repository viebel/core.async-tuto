(ns tuto.chan
  (:require [clojure.core.async :refer [<!! >!! chan put! take!]]))


;; ---- Short Intro ---
;; core.async offers two main benefits:
;; 1. rich semantics for asynchronous communications (channels ...)
;; 2. lighweight threads

;; in Clojurescript it allows to write asynchronous code in a way to look synchronous (similar to async/await but several years before it was available in javascript)

;;; ---- Channels ----
;; We can write on a channel either in a blocking or non-blocking way
;; Functions that are blocking have names ending with `!!`
;; When using blocking functions we must use different threads
;; We can write any kind of data on a channel except nil which has the meaning of closing the channel

;;; --- Code Examples ----
;; channel with no buffer

(defn write-and-wait []
  (let [c (chan)
        f (future (>!! c "a")
                  (println "done")
                  1)]
    [c f]))

(comment
  (def data (write-and-wait))
  (def c (first data))
  c
  (def f (second data))
  ;; the thread is blocked until we read from the channel
  f
  (def a (<!! c))
  @f
  (deref f))

;; put! writes asynchronously - never blocks 
(defn write-no-blocking []
  (let [c (chan)]
    (put! c "hello")
    (<!! c)))

(comment
  (write-no-blocking))

(defn write-messages [n buffer-size]
  (let [c (if (zero? buffer-size)
            (chan)
            (chan buffer-size))]
    (dotimes [i n]
      (put! c (str "hello " i)))))

(comment
  (write-messages 1024 0)
  (write-messages 1025 1))

;; read messages with take! and a callback function

(defn create-channel-and-read []
  (let [c (chan)]
    (take! c (fn [x]
               (println (str "taken: " x))))
    c))

(comment
  (do
    (def c (create-channel-and-read))
    (put! c "hi")
    (println "done")))

;;; --- Exercise --
;;; Write code that reads forever from a channel
;;; Each time we write a message to the channel, the message is echoed to the console

(defn create-channel-and-read-forever []
  (let [c (chan)]
    (future (while true
              (take! c #(println (str "taken: " %)))))
    c))

(comment
  (def c (create-channel-and-read-forever))
  (>!! c "hi"))
