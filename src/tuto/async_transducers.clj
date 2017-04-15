(ns tuto.async-transducers
  (:require [clojure.core.async :refer [pipeline go go-loop <! <!! >!! >! chan put! dropping-buffer buffer sliding-buffer thread]]))


(let [c (chan 1 (map inc))]
  (go (println "c: " (<! c)))
  (put! c 10))


(let [c (chan 1)
      b (chan 1)]
  (pipeline 1 b (map inc) c)
  (go (println "b: " (<! b)))
  (put! c 10))

