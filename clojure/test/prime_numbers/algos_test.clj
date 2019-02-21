(ns prime-numbers.algos-test
  "Tests for prime number generating algorithms."
  (:require
   [clojure.spec.alpha  :as spec]
   [clojure.test        :as test :refer [deftest is]]
   [prime-numbers.algos :as algos])
  (:import
   [clojure.lang ExceptionInfo]))


(def ^:private first-twenty-primes
  [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71])


(deftest test-primes-vec
  "Test that the base-cases of prime numbers is the correct size, and
   actually prime.  Intended to protect against accidental tampering."

  (is (= 20 (count first-twenty-primes)))

  (is (every? #(.isProbablePrime (biginteger %) 1) first-twenty-primes)))
