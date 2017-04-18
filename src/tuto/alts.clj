(ns tuto.alts
  (:require [clojure.core.async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap pub sub timeout alts! alts!! alt! alt!!]]))


;; alts and alt

;; wait until timeout

(let [c (chan)
      t (timeout 1000)]
  (= t (second (alts!! [c t]))))



(let [c (chan)
      t (timeout 1000)]
  (put! c "hello")
  (= t (second (alts!! [c t]))))


(let [c (chan)
      t (timeout 1000)]
  (alt!!
    t :timeout
    [c] ([val ch] val)))

(let [c (chan)
      t (timeout 1000)]
  (alt!!
    t :timeout
    [c] ([val ch] val)
    :default 42))


(let [c (chan)
      t (timeout 1000)]
  (put! c "hello")
  (alt!!
    t :timeout
    [c] ([val ch] val)
    :default 42))


;; slurp-with-timeout
;;Write a function that that slurps a url with a timeout parameter


;; memoize-async
;; write a function that memoizes a function that returns a channel


;; go-map
;; write a function that applies an async function to a collection e.g. slurp-async

;; Questions
;; alts! vs. alt! http://stackoverflow.com/questions/22085497/in-clojure-core-async-whats-the-difference-between-alts-and-alt


