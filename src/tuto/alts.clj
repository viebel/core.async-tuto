(ns tuto.alts
  (:require [clojure.core.async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap pub sub timeout alts! alts!! alt! alt!!]]))

(comment


  ;; alts and alt

  ;; wait until timeout


  (<!! (timeout 1000))

  (let [c (chan)
        t (timeout 1000)]
    (= t (second (alts!! [c t]))))


  (let [c (chan)
        t (timeout 1000)
        [val ch] (alts!! [c t])]
    (cond 
      (= ch t) :timeout
      (= ch c) val))


  (let [c (chan)
        t (timeout 1000)]
    (put! c "hello")
    (first (alts!! [c t])))


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
      :default 42)))


  ;; slurp-with-timeout
  ;;Write a function that that slurps a url with a timeout parameter


(defn slurp-cb [url cb]
  (future (cb (slurp url))))

(defn slurp-async [url]
  (let [c (chan)]
    (slurp-cb url #(put! c %))
    c))

(comment
  (<!! (slurp-async "http://www.google.com")))


(defn slurp-timeout [url timeout-msec]
  (let [c (slurp-async url)
        t (timeout timeout-msec)]
    (alt!!
      t :timeout
      [c] ([val ch] val))))

(comment
  (slurp-timeout "http://www.google.com" 100)
  (slurp-timeout "http://www.google.com" 5000)
  )


(comment
    ;; memoize-async
  ;; write a function that memoizes a function that returns a channel


  (<!! ((memoize-async slurp-async) "http://www.cnn.com")) ;; slow

  (<!! ((memoize-async slurp-async) "http://www.cnn.com")) ;; fast


  ;; map-async
  ;; write a function that applies an async function to a collection e.g. slurp-async

)

(defn map-async
  [f coll]
  (let [chans (map f coll)
        chan->coll (zipmap chans coll)]
    (go-loop [res {} channels chans]
      (if (empty? channels)
        (map res coll)
        (let [[x c] (alts! channels)]
          (recur (assoc res (chan->coll c) x)
                 (remove #{c} channels)))))))

(comment

  
  (<!! (map-async slurp-async ["http://cnn.com" "http://google.com"]))

  (time (def a (<!! (map-async (fn [_] (timeout 100)) (range 100000)))))

  
  ;; Questions
  ;; alts! vs. alt! http://stackoverflow.com/questions/22085497/in-clojure-core-async-whats-the-difference-between-alts-and-alt


  
  )
