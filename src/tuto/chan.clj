(ns tuto.chan
  (:require [clojure.core.async :refer [put! <!! >!! chan thread alts!! take!]]))

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
  (def a (<!! c)) a
  @f
  (deref f))

;; put! writes asynchronously - never blocks (until...)
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
               (println "a current thread: " (Thread/currentThread))
               (println (str "taken: " x))
               (println "b current thread: " (Thread/currentThread))
               (println (str "after sleep " x))))
    c))

(comment
  (do
    (def c (create-channel-and-read))
    (println "m current thread: " (Thread/currentThread))
    (put! c "hi")
    (println "done"))
  (dotimes [i 1025]
    (put! c "hi"))
  )

(defn create-channel-and-read-forever []
  (let [c (chan)]
    (future (while true
              (println (.getName (Thread/currentThread)))
              (take! c #(println (str "taken: " % " on " (.getName (Thread/currentThread)))))))
    c))



(comment
  (def c (create-channel-and-read-forever))
  (>!! c "hi"))








