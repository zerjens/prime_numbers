(ns prime-numbers.algos.prime-algos-test
  "Tests for prime number generating algorithms."
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.test       :as test :refer [deftest is]]
   [prime-numbers.algos.prime-algos :as algos])
  (:import
   [clojure.lang ExceptionInfo]))


(def ^:private first-twenty-primes
  [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71])


(deftest test-primes-vec
  "Test that the base-cases of prime numbers is the correct size, and
   actually prime.  Intended to protect against accidental tampering."

  (is (= 20 (count first-twenty-primes)))

  (is (every? #(.isProbablePrime (biginteger %) 1) first-twenty-primes)))


(deftest valid-range-test
  "This iterates over the edge cases of the spec for valid prime tests."

  ;; against a char 'a'
  (is (not (spec/valid? :prime-numbers.algos.prime-algos/valid-prime-fn-input \a)))

  (is (thrown? ExceptionInfo
               (spec/assert :prime-numbers.algos.prime-algos/valid-prime-fn-input \a)))

  ;; against a string "12"
  (is (not (spec/valid? :prime-numbers.algos.prime-algos/valid-prime-fn-input "12")))

  (is (thrown? ExceptionInfo
               (spec/assert :prime-numbers.algos.prime-algos/valid-prime-fn-input "12")))

  ;; against an empty map
  (is (not (spec/valid? :prime-numbers.algos.prime-algos/valid-prime-fn-input {})))

  (is (thrown? ExceptionInfo
               (spec/assert :prime-numbers.algos.prime-algos/valid-prime-fn-input {})))

  ;; against a negative integer.
  (is (not (spec/valid? :prime-numbers.algos.prime-algos/valid-prime-fn-input -1)))

  (is (thrown? ExceptionInfo
               (spec/assert :prime-numbers.algos.prime-algos/valid-prime-fn-input -1)))

  ;; against a non-positive integer.
  (is (not (spec/valid? :prime-numbers.algos.prime-algos/valid-prime-fn-input 0)))

  (is (thrown? ExceptionInfo
               (spec/assert :prime-numbers.algos.prime-algos/valid-prime-fn-input 0)))

  ;; against a rational number.
  (is (not (spec/valid? :prime-numbers.algos.prime-algos/valid-prime-fn-input 1/2)))

  (is (thrown? ExceptionInfo
               (spec/assert :prime-numbers.algos.prime-algos/valid-prime-fn-input 1/2)))

  ;; valid, positive integer.
  (is (spec/valid? :prime-numbers.algos.prime-algos/valid-prime-fn-input 1))

  (is (spec/assert :prime-numbers.algos.prime-algos/valid-prime-fn-input 1)))


(deftest check-against-primes-test
  "Edge case tests for custom primality testing."
  (let [prime? #'prime-numbers.algos.prime-algos/check-against-primes]

    ;; to be sure, order does matter in list equality.
    (is (= '(2)          (prime? 2  '())))
    (is (= '(3 2)        (prime? 3  '(2))))
    (is (= '(3 2)        (prime? 4  '(3 2))))
    (is (= '(5 3 2)      (prime? 5  '(3 2))))
    (is (= '(5 3 2)      (prime? 6  '(5 3 2))))
    (is (= '(7 5 3 2)    (prime? 7  '(5 3 2))))
    (is (= '(7 5 3 2)    (prime? 8  '(7 5 3 2))))
    (is (= '(7 5 3 2)    (prime? 9  '(7 5 3 2))))
    (is (= '(7 5 3 2)    (prime? 10 '(7 5 3 2))))
    (is (= '(11 7 5 3 2) (prime? 11 '(7 5 3 2))))))

(deftest primes-iterative-test
  "Iterate through first through 20th prime for basic `primes-iterative`"

  (is (thrown? ExceptionInfo (algos/primes-iterative 0)))

  (doall
   (for [x (range 1 21)]
     (do
       (is (= x (count (algos/primes-iterative x))))
       (is (= (algos/primes-iterative x)
              (take x first-twenty-primes))))))

  (is (not (= (algos/primes-iterative 21)
              first-twenty-primes))))

(deftest primes-iterative-test'
  "Iterate through first through 20th prime for basic `primes-iterative'`"

  (is (thrown? ExceptionInfo (algos/primes-iterative' 0)))

  (doall
   (for [x (range 1 21)]
     (do
       (is (= x (count (algos/primes-iterative' x))))
       (is (= (algos/primes-iterative' x)
              (take x first-twenty-primes))))))

  (is (not (= (algos/primes-iterative' 21)
              first-twenty-primes))))
