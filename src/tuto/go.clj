(ns tuto.go
  (:require [clojure.core.async :refer [go <! >! chan put! dropping-buffer buffer sliding-buffer <!! timeout thread]]))


;; The go macro
;; (go (println "got" (<! c))) => something like:
;; (take! c (fn [x] (println "got" x)))

;; it uses its own thread pool
;; go processes are lightweight threads

;; Go blocks
(def c (chan))
(do (go (>! c "a")
        (println "done"))

    (println "aaaa"))
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
        (print (str "done -- " i "\n"))))
  (go
    (print "read\n")
    (<! c)))


;;go returns a channel

(<!! (go (let [x 10]
           (<! (timeout 5000))
           (* x x))))

;; Questions
;; 1. what's the difference between (go (<! ..)) and (thread (<!! ...))?


(dotimes [i 10]
  (go
    (<! (timeout 1000))
    (println i)))


(do
  (def d (promise))
  (def c (chan))
  (let [t (. System (nanoTime))]
    (put! c 1 (fn [_]
                  (deliver d (- (. System (nanoTime)) t))))
    (println "taken value, delivery time - same thread"  [(<!! c) @d])))

(do
  (def d (promise))
  (def c (chan))
  (let [t (. System (nanoTime))]
    (put! c 1
                (fn [_]
                  (deliver d (- (. System (nanoTime)) t))))
    (println "taken value, delivery time - same thread"  [(<!! c) @d])))

(let [c (chan)]
  (dotimes [i 1]
    (put! c i)
    (let [t (. System (nanoTime))]
      (go (let [val (<! c)
                elapsed (- (. System (nanoTime)) t)]
            (println (str "taken value, delivery time with go: -- " val " time: " (float  (/ elapsed 1e6)) "ms" "\n")))))))


(let [c (chan)]
  (dotimes [i 1]
    (put! c i)
    (let [t (. System (nanoTime))]
      (future (let [val (<!! c)
                elapsed (- (. System (nanoTime)) t)]
                (println (str "taken value, delivery time with future -- " val " time: " (float  (/ elapsed 1e6)) "ms" "\n")))))))

(do
  (def c (chan))
  (put! c 2)
  (let [t (. System (nanoTime))]
    (thread
      (<!! c)
      (let [elapsed (- (. System (nanoTime)) t)]
        (println (str "taken value, delivery time with future -- " val " time: " (float  (/ elapsed 1e6)) "ms" "\n"))))))


(macroexpand '(go
                (+ 1 2)
                (+ 3 4)))

(defn f []
  (+ 1 2)
  (+ 3 4))

(go (f))


