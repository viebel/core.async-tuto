(ns tuto.mult
  (:require [clojure.core.async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap]]))


(comment


  ;; sometimes, we want to have several independent consumers of the same channel
  ;; the channel needs to be multiplied

  (def c (chan))
  (def mult-c (mult c))

  (def cx (chan))
  (tap mult-c cx)
  (put! c "hello")
  (go-loop []
    (println "read from cx: " (<! cx))
    (recur))


  ;; if the message is write before the `tap` it is not buffered.
  (def cy (chan))
  (put! c "hello")
  (tap mult-c cy)
  (go-loop []
    (println "read from cy: " (<! cy))
    (recur))

  (put! c "bonjour")


  (untap mult-c cx)
  (untap mult-c cy)







  )
