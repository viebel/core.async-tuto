(ns tuto.go
  (:require [clojure.core.async :refer [go <! >! chan put! dropping-buffer buffer sliding-buffer]]))


;; Go blocks
(def c (chan))
(go (>! c "a")
    (println "done"))

;; the previous process is blocked until we read
(go (<! c))

;; buffer
(let [c (chan 10)]
  (go (>! c "a")
      (println "done")))

;; the previous process is not blocked because of the buffer


;; how many messages are written?
(let [c (chan 10)]
  (go (dotimes [i 15]
        (>! c "a")
        (println "done -- " i)))
  (go (<! c)))




