(ns tuto.async-transducers
  (:require [clojure.core.async :refer [pipeline go go-loop <! <!! >!! >! chan put! dropping-buffer buffer sliding-buffer thread]]))


(let [c (chan 1 (map inc))]
  (put! c 10)
  (<!! c))

(let [c (chan 1)
      b (chan 1)]
  (pipeline 1 b (map inc) c)
  (put! c 10)
  (<!! b))

