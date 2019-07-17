(ns tuto.go
  (:require [clojure.core.async :as async
             :refer [go <! >! chan put! dropping-buffer buffer sliding-buffer <!! timeout thread close!]]))


 ;; The go macro
 ;; (go (println "got" (<! c))) => something like:
 ;; (take! c (fn [x] (println "got" x)))

 ;; it uses its own "thread" pool
 ;; go processes are lightweight threads

  ;; Go blocks
(defn go-write []
  (let [c (chan)]
    (go (>! c "a")
        (println "done in go"))
    (println "AAAAA")
    c))

(comment
  (def c (go-write))
  ;; the previous process is blocked until we read
  (<!! c))

(defn go-write-buffer []
  (let [c (chan 10)]
    (go (>! c "a")
        (println "done"))))

(comment
  ;; the previous process is not blocked because of the buffer
  (go-write-buffer))


;; how many messages are written?
(defn go-write-and-read []
  (let [c (chan (sliding-buffer 10))]
    (go (dotimes [i 15]
          (>! c (str "a " i))
          (println (str "done -- " i))))
    (go
      (println (str "read " (<! c))))))

(comment
  (go-write-and-read))

(defn square-async [x]
  (let [c (chan)]
    (go  (<! (timeout 2000))
         (put! c (* x x)))
    c))

(comment
  
  (<!! (timeout 2000))
  (def c (square-async 9))
  (<!! c))

(defn go-calc [x]
  ;;go returns a channel
  (go (<! (timeout 2000))
      (* x x)))

(comment
  (<!! (go (<! (timeout 2000))
           (* 9 9)))
  (def cc (async/map inc [(go-calc 2) (go-calc 4)]))
  (map + (range 10) (range 100 1000))
  (def num-c (async/to-chan (range 10)))
  (def num-d (async/to-chan (range 100 1000)))
  (def inc-num-c (async/map + [num-c num-d]))
  (<!! inc-num-c)
  (<!! cc)
  (<!! (go-calc 12)))


(defn go-sleep-n-times [n]
  (dotimes [i n]
    (go
      (<! (timeout 1000))
      (println i))))

(defn go-go-sleep-n-times [n]
  (go
    (dotimes [i n]
      (<! (timeout 1000))
      (println i))))

(comment
  (go-sleep-n-times 10)
  (go-go-sleep-n-times 10))


(defn thread-sleep-n-times [n]
  (dotimes [i n]
    (thread
      (<!! (timeout 1000))
      (println i))))

(comment
  (thread-sleep-n-times 10))

(comment
 ;; performances
  (do
    (let [c (chan)
          _ (put! c 2)
          t (. System (nanoTime))
          elapsed @(future
                     (let [_ (<!! c)]
                       (- (. System (nanoTime)) t)))]
      (println (str "taken value, delivery time with thread --  time:" (float  (/ elapsed 1e6)) "ms" "\n")))

    (let [c (chan)
          _ (put! c 1)
          t (. System (nanoTime))]
      (go  (let [elapsed (<! (go (let [val (<! c)]
                                    (- (. System (nanoTime)) t))))]
             (println (str "taken value, delivery time with go: -- " val " time: " (float  (/ elapsed 1e6)) "ms" "\n"))))))

  (do
    (let [elapsed-total (loop [i 20000
                               elapsed-total 0]
                          (if (zero? i)
                            elapsed-total
                            (let [c (chan)
                                  _ (put! c 1)
                                  t (. System (nanoTime))]
                              (let [elapsed (<!! (go (let [_ (<! c)]
                                                       (- (. System (nanoTime)) t))))]
                                (recur (dec i) (+ elapsed-total elapsed))))))]
      (println (str "taken value, delivery time with go: --  time:" (float  (/ elapsed-total 1e6)) "ms" "\n")))

    (let [elapsed-total (loop [i 20000
                               elapsed-total 0]
                          (if (zero? i)
                            elapsed-total
                            (let [c (chan)
                                  _ (put! c 1)
                                  t (. System (nanoTime))]
                              (let [elapsed (deref (thread (let [_ (<! c)]
                                                       (- (. System (nanoTime)) t))))]
                                (recur (dec i) (+ elapsed-total elapsed))))))]
      (println (str "taken value, delivery time with go: --  time:" (float  (/ elapsed-total 1e6)) "ms" "\n")))

    
    )

  (let [iterations 100]
    [(let [t (. System (nanoTime))]
        (dotimes [_ iterations]
          (let [c (chan)
                _ (put! c 1)]
            (<!! (deref (future c)))))
        (let [elapsed (- (. System (nanoTime)) t)]
          (str (float  (/ elapsed 1e6)) "ms")))

     #_(let [t (. System (nanoTime))]
       (dotimes [_ iterations]
         (let [c (chan)
               _ (put! c 1)]
           (<!! (thread (<! c)))))
       (let [elapsed (- (. System (nanoTime)) t)]
         (str (float  (/ elapsed 1e6)) "ms")))
     
     (let [t (. System (nanoTime))]
       (dotimes [_ iterations]
         (let [c (chan)
               _ (put! c 1)]
           (<!! (go (<! c)))))
       (let [elapsed (- (. System (nanoTime)) t)]
         (str (float  (/ elapsed 1e6)) "ms")))])


  )
