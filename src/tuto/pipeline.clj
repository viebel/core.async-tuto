(ns tuto.pipeline
  (:require [clojure.core.async :refer [go go-loop chan close! timeout >! <! put! take!]]))

;;; utility pipeline step func

(defn pipeline-step
  "receives an input channel and a function f
  f receives a fwd function and is supposed to call fwd with each result
  returns an output channel where the "
  [in f]
  (let [out (chan)
        fwd #(go (>! out %))]
    (go-loop []
      (if-let [data (<! in)]
        (do (try
              (f fwd data)
              (catch Exception e
                (println e)))
            (recur))
        (go (close! out))))
    out))

;;; testable, stateless, and hopefully side-effects-free, pipeline steps

(defn load-from-db [fwd value]
  (let [records (repeat 10 value)]
    (fwd records)))

(defn to-batches [fwd records]
  (doseq [batch (partition-all 4 records)]
    (fwd batch)))

(defn fetch-from-network [fwd batch]
  (let [http-post-async (fn [body cb] (cb (map #(do {:value %}) body)))]
    (http-post-async batch fwd)))

(defn calc-status [fwd batch]
  (let [records (map #(assoc % :status "great") batch)]
    (fwd records)))

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
    (doseq [id (range 10)]
      (>!! channel id))
    (close! channel)))
