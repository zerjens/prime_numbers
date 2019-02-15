 (ns prime-numbers.main-test
  (:require
   [clojure.test       :as test :refer [deftest is]]
   [prime-numbers.main :as main])
  (:import
   [clojure.lang ExceptionInfo]))

(deftest algo-selection-test
  "checks that the method dispatch works correctly"

  (let [pi  prime-numbers.algos.iterative/primes-iterative
        pi' prime-numbers.algos.iterative/primes-iterative'
        pl  prime-numbers.algos.parallel/primes-parallel]

    (is (= pi  (main/select-algo nil)))

    (is (= pl  (main/select-algo :parallel)))

    (is (= pi' (main/select-algo :alt)))))

(deftest print-table-test
  "Redirect stdout to test here"
  (let [out-str (with-out-str (main/print-table! '(2 3 5 7) '(2 3 5 7)))]


    (let [rows (clojure.string/split out-str #"\n")]

      ;; 5 - includes header
      (is (= 5 (count rows)))

      ;; 5 - includes space
      (is (= 5 (count (clojure.string/split (first rows) #"\t")))))))


