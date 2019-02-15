(ns prime-numbers.algos.parallel-test
  "Tests for prime number generating algorithms."
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.test       :as test :refer [deftest is]]
   [prime-numbers.algos.parallel :as algos])
  (:import
   [clojure.lang ExceptionInfo]))


(deftest valid-range-test
  "This iterates over the edge cases of the spec for valid prime tests."

  (is (spec/valid? :prime-numbers.algos.parallel/valid-prime-parallel-input 1))

  (is (spec/assert :prime-numbers.algos.parallel/valid-prime-parallel-input 1)))

(deftest relative-primes-test
  "Edge case tests for custom primality testing."
  (let [prime? #'prime-numbers.algos.parallel/relatively-prime?]

    ;; to be sure, order does matter in list equality.
    (is (prime? (biginteger 3) '()))
    (is (prime? (biginteger 5) '(3)))
    (is (prime? (biginteger 7) '(5 3)))

    ;; 9 is composite
    (is (not (prime? (biginteger 9) '(7 5 3))))
    (is (prime? (biginteger 11) '(7 5 3)))
    (is (prime? (biginteger 13) '(11 7 5 3)))

    ;; 15 is composite
    (is (not (prime? (biginteger 15) '(11 7 5 3))))
    (is (prime? (biginteger 17) '(11 7 5 3)))
    (is (prime? (biginteger 19) '(17 11 7 5 3)))

    ;; 21 is composite
    (is (not (prime? (biginteger 21) '(19 17 11 7 5 3))))))

(deftest psieve-test
  "Inductive profs start with a base case and then show that
   f(n) => f(n=1)... This is the base case and a few more."

  (let [psieve #'prime-numbers.algos.parallel/psieve]

    ;; primes 3 < 9, should yield 5 and 7 only.
    (is (= #{5 7}
           (psieve (biginteger 3) '() #{})))

    ;; 5 < primes < 25, and not in the previous result.
    (is (= #{11 13 17 19 23}
           (psieve (biginteger 5) '(3) #{7})))

    ;; 7 < primes < 49, and not in the previous result.
    (is (= #{29 31 37 41 43 47}
           (psieve (biginteger 7) '(5 3) #{11 13 17 19 23})))

    ;; 11 < primes < 131, and not in the previous result.
    (is (= #{53 59 61 67 71 73 79 83 89 97 101 103 107 109 113}
           (psieve (biginteger 11)
                   '(7 5 3)
                   #{13 17 19 23 29 31 37 41 43 47})))))

(def ^:private first-twenty-primes
  [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71])


(deftest test-primes-vec
  "Test that the base-cases of prime numbers is the correct size, and
   actually prime.  Intended to protect against accidental tampering."

  (is (= 20 (count first-twenty-primes)))

  (is (every? #(.isProbablePrime (biginteger %) 1) first-twenty-primes)))

(deftest primes-parallel-lazy
  "First test that we generate primes from 3 on correctly"
  (let [lazy-primes #'prime-numbers.algos.parallel/primes-parallel-lazy]
    (doall
     (for [x (range 1 19)]
       (is (= (take x (lazy-primes))
              (take x (rest first-twenty-primes))))))))

(deftest primes-parallel-lazy
  "check that the full sequence is realized correctly"
  (doall
   (for [x (range 1 21)]
     (is (= (algos/primes-parallel x)
            (take x first-twenty-primes))))))
