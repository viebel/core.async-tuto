(ns tuto.api
  (:require [clojure.core.async :as async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap pub sub]]))


;; instead of callbacks we can have code that looks synchronous
;; we can escape the callback hell

(defn inc-cb [x cb]
  (future
    (cb (inc x))))


(comment
    
  (inc-cb 50
          #(println "inc-thread: " %)))


(defn inc-async [x]
  (let [c (chan)]
    (inc-cb x #(put! c %))
    c))


(comment


  (println "do-inc-async" (<!! (inc-async 19)))

  (<!! (inc-async 19))


  (slurp "http://www.google.com")
;;; Exercises

  ;; async-slurp
  ;; write an async version of slurp i.e. that executes slurp in a thread
  ;; write a function `async-slurp` that receives a url and returns a channel that will receives the contents of the url
  (defn slurp-cb [url cb]
    (future ))

  (defn slurp-async [url]
    (go (slurp url)))

  (go (println "cnn.com: " (<! (slurp-async "http://www.google.com"))))

;;;;

  (comment
    (defn slurp-cb [url cb]
      (future (cb (slurp url))))

    (defn slurp-async [url]
      (let [c (chan)]
        (slurp-cb url #(put! c %))
        c))

    (go (println "cnn.com: " (async/map identity  (slurp-async "http://www.cnn.com")))))


  (<!! (slurp-async "http://www.cnn.com"))
;;; Write a loop that receives 10 times an number from the user and prints the square of the number
;;; Without creating threads
;;; Without blocking the main thread 


;;; 1. write with callbacks
;;; 2. write with go-loop


  ;; Same exerices with 4 loops
  ;; 1st loop: square
  ;; 2nd loop: inc
  ;; 3rd loop: dec
  ;; 4th loop: (* x (+ x 2))

;;; don't even try with callbacks
  (read)
  )
