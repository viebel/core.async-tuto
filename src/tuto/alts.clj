(ns tuto.alts
  (:require [clojure.core.async :refer [go-loop go >! <! put! <!! >!! chan dropping-buffer buffer sliding-buffer thread mult tap untap pub sub]]))


;; memoize-async
;; write a function that memoizes a function that returns a channel


;; go-map
;; write a function that applies an async function to a collection e.g. slurp-async


