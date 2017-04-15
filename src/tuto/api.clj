(ns tuto.api
  (:require [clojure.core.async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap pub sub]]))


;; instead of callbacks we can have code that looks synchronous
;; we can escape the callback hell

(defn inc-thread [x cb]
  (thread
    (cb (inc x))))

(inc-thread 5 #(println "inc-thread: " %))


(defn inc-async [x]
  (let [c (chan)]
    (inc-thread x #(put! c %))
    c))

(println "do-inc-async" (<!! (inc-async 19)))

(<!! (inc-async 19))


;;; Exercises

;; async-slurp
;; write an async version of slurp i.e. that executes slurp in a thread
;; write a function `async-slurp` that receives a url and returns a channel that will receives the contents of the url
(defn slurp-cb [url cb]
  (future ))

(defn slurp-async [url]
  (let [c (chan)]))

(go (println "cnn.com: " (<! (slurp-async "http://www.cnn.com"))))




;;;;

(comment
  (defn slurp-cb [url cb]
    (future (cb (slurp url))))

  (defn slurp-async [url]
    (let [c (chan)]
      (slurp-cb url #(put! c %))
      c))

  (go (println "cnn.com: " (<! (slurp-async "http://www.cnn.com")))))

