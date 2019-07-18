(ns tuto.pipeline-async
  (:require [clojure.core.async :refer [go go-loop chan close! timeout >! <! put! take! >!! <!!]]))

;;; utility pipeline step func

(defn pipeline-step
  "receives an input channel and a function f
  returns an output channel
  calls f with each message from the input channel
  f is supposed to write its output to the output channel"
  [in f]
  (let [out (chan)]
    (go
      (loop []
        (when-let [data (<! in)]
          (try
            (f out data)
            (catch Exception e
              (println "An exception occured" e)))
          (recur)))
      (close! out))
    out))

;;; testable, stateless, and hopefully side-effects-free, pipeline steps

(defn load-from-db [c value]
  (let [records (repeat 10 value)]
    (put! c records)))

(defn to-batches [c records]
  (doseq [batch (partition-all 4 records)]
    (put! c batch)))

(defn fetch-from-network [c batch]
  (let [http-post-async (fn [body c] (put! c (map #(do {:value %}) body)))]
    (http-post-async batch c)))

(defn calc-status [c batch]
  (let [records (map #(assoc % :status "great") batch)]
    (put! c records)))

(defn store-to-db [_ records]
  (println (format "%d records updated (sample: %s)" (count records) (first records))))

;;; create pipeline and pour data in
;;; could be in a quartz job
;;; channel could be handled in global context,
;;; in which case init/close is irrelevant here (just an example)

(comment
  (let [channel (chan)
        pipeline (-> channel
                     (pipeline-step load-from-db)
                     (pipeline-step to-batches)
                     (pipeline-step fetch-from-network)
                     (pipeline-step calc-status)
                     (pipeline-step store-to-db))]
    (doseq [id (range 2)]
      (>!! channel id))
    (close! channel))
  )
