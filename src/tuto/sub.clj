(ns tuto.sub
  (:require [clojure.core.async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap pub sub]]))


;; sometimes, we want to have several independent consumers of the same channel
;; publish and subscribe

(def c (chan))

(def pub-c (pub c :route))

(let [cx (chan)]
  (sub pub-c "foo" cx)
  (go-loop []
    (println "foo: " (<! cx))
    (recur)))

(let [cx (chan)]
  (sub pub-c "bar" cx)
  (go-loop []
    (println "bar: " (<! cx))
    (recur)))



(put! c {:route "foo" :data 22})
(put! c {:route "bar" :data 555})


;; Exercises
;; Create a system where commands are passed to the appropriate executor
;; command types:
;;    - numbers e.g (* 2 3)
;;    - sequences e.g (first [1 2 3])
;; There will be multiple executors of the same type





