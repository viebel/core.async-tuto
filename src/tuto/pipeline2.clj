(ns tuto.pipeline2
  (:require [clojure.core.async :refer [<! <!! >! >!! chan go-loop onto-chan]]
            [clojure.core.async.impl.protocols :refer [ReadPort]]))

(comment
  (def algo (map (fn [x]
                   (println "yes.. " x)
                   (inc x))))
  (def c (chan 100 algo))
  (>!! c 10)
  (<!! c)
  (onto-chan)
  )

(defn pipe-ext [in out]
  (go-loop []
    (when-some [v (<! in)]
      (>! out v)
      (recur))))

(defn map-ext
  "Read from `in` some `x` and put on `out` something according to `val`, which is `(f x)`:
  - when `val` is a seq or a vec: put on `out` all the elements of `val`
  - when `val` is a readable channel: put all the elements of the channel
  - otherwise: put `val` on `out`
  "
  [in f out]
  (go-loop []
    (when-some [val' (<! in)]
      (let [val (f val')]
        (cond
          (or (seq? val)
              (vector? val))              (<! (onto-chan out val))
          (extends? ReadPort (class val)) (<! (pipe-ext val out))
          :else                           (>! out val)))
      (recur))))

(defn pipeline< [desc c]
  (let [p (partition 2 desc)]
    (reduce
      (fn [prev-c [n f]]
        (let [out-c (chan n)]
          (dotimes [_ n]
            (map-ext prev-c f out-c))
          out-c))
      c
      p)))


(comment
  (let [c (chan 10)
        a (atom [])]
    (>!! c 42)
    (let [z (<!! (pipeline< [1 inc
                             1 range
                             1 (fn [x] (swap! a conj x) x)
                             2 dec
                             1 str]
                            c))]
      (println @a)
      z)
    ))

(comment
  
  (defn dechunk [s]
    (lazy-seq
      (when-let [[x] (seq s)]
        (cons x (dechunk (rest s))))))
  (type (map (fn [x]
               (Thread/sleep 100)
               x)
             (dechunk (range 100))))
  (time (first (map (fn [x]
                      (Thread/sleep 100)
                      x)
                    (dechunk (range 100)))))
  (time (first (map (fn [x]
                      (Thread/sleep 100)
                      x)
                    (range 100)))))
