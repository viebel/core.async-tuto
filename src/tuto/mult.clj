(ns tuto.mult
  (:require [clojure.core.async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap]]))


;; sometimes, we want to have several independent consumers of the same channel
;; the channel needs to be multiplied

(def c (chan))
(def mult-c (mult c))

(put! c "hello")

(def cx (chan))
(tap mult-c cx)
(go-loop []
  (println "read from cx: " (<! cx))
  (recur))


(def cy (chan))
(tap mult-c cy)
(go-loop []
  (println "read from cy: " (<! cy))
  (recur))

(put! c "bonjour")
;(untap mult-c cx)
;(untap mult-c cy)







