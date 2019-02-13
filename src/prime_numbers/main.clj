(ns prime-numbers.main
  "Several procedures for generating lists of prime numbers.

   Intended to print out a table of the primes and ..."
  (:require
   [prime-numbers.algos.prime-algos :as algos])
  (:import
   [java.lang NumberFormatException])
  (:gen-class))

(defmulti  select-algo identity)

;; just returning the function makes this easily testable.
(defmethod select-algo :alt
  [a]
  algos/primes-iterative')

(defmethod select-algo :default
  [a]
  algos/primes-iterative)


(defn print-table!
  "writes a table of the products of two collections to stdout. "
  [row col]

  ;; print the head of the table as just the 'row' collection.
  (doseq [r (-> (map int row)
                (as-> $ (cons " " $))
                (interleave (repeat "\t"))
                butlast
                (concat "\n"))]
    (print r))

  ;; print the rest of the table.
  (doseq [c col]
    (doseq [x (-> (map #(int (* c %)) row)
                  (as-> $ (cons (int c) $))
                  (interleave (repeat "\t"))
                  butlast
                  (concat "\n"))]
      (print x))))


(defn -main
  "Entry point of program."
  [& args]
  
  (let [algo       (select-algo (keyword (second args)))
        iterations (try (Integer/parseInt (first args))
                        (catch NumberFormatException nfe
                          10))

        primes     (algo iterations)]

    ;; just print out the table.
    (print-table! primes primes)))
