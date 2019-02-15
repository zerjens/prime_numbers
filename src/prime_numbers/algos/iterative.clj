(ns prime-numbers.algos.iterative
  "Several procedures for generating lists of prime numbers.

   Intended to print out a table of the primes and ..."
  (:require
   [clojure.spec.alpha :as spec])
  (:import
   [java.math BigInteger]))

;; Ensures we throw an ExceptionInfo when a spec isn't valid.
;; overrides command-line args for enable-assertions, evaled
;; when namespace is loaded.
(spec/check-asserts true)


;; This is intended to be a compositional style function, where
;; all other prime-finding functions are composed of this spec:
;; Ns qualified spec, applied in ':pre' section of relevant fns.
(spec/def ::valid-prime-fn-input pos-int?)


(defn- check-against-primes
  "This function is used when reducing/iterating over a sequence
   of potential prime numbers, assuming a collection of these
   primes is aggregated along the way...

   One speed up here is that we only need to check primes up to
   the square root of the input number 'n'.  No need to spec here
   but tested for edge cases such as an empty collection.
   Some style points from:
      https://stuartsierra.com/2018/07/06/threading-with-style

   Returns an updated collection of primes if 'n' is prime"
  [^BigInteger n primes]
  (let [prime? (->> primes
                    (drop-while #(<= (inc (.sqrt n)) %))
                    (every?     #(not= 0 (rem n %))))]
    (if prime? (cons n primes) primes)))


(defn primes-iterative
  "This is the first 'brute-force' method.  It recursively loops
   through an iterator to find the 'nth' prime number specified.

   Looping is not lazy, and using BigInteger, it shouldn't thrown
   an IntegerOverflow or StackOverflow.  The time complexity for
   this is sub-optimal though, since the distribution of primes
   is proportional, and provably upper-bounded by the logarithmic
   integral.  This means our time complexity is super-linear,
   which is a little hard to show directly:
     https://math.stackexchange.com/a/94877

   pre spec'd, thanks to Alex Miller:
     https://groups.google.com/d/msg/clojure/H9tk04sSTWE/DErRB4_FDAAJ
   however this may not be the best approach:
     https://github.com/bbatsov/clojure-style-guide/issues/85

   input `nth-prime` - how many primes we want to return.
   output - ordered list of primes of size 'n'."
  [nth-prime]

  {:pre [(spec/assert ::valid-prime-fn-input nth-prime)]}

  ;; The initial values for this are a lazy list of potential prime numbers
  ;; destructured into the head and tail.  Coercion keeps each element in
  ;; this list of type `java.lang.BigInteger` which prevents int overflow.
  (loop [[hd & tl] (iterate #(+ 2 %) (biginteger 3))
         primes    '()]

    (if (= (dec nth-prime) (count primes))

      ;; since we cons'ed larger prime numbers to the primes list
      ;; reverse it before returning to give ascending order.
      (cons (biginteger 2) (reverse primes))

      ;; Normal applicative order means we evaluate the
      ;; nested statements before passing them to recur.
      (recur tl (check-against-primes (biginteger hd) primes)))))


(defn primes-iterative'
  "Analog to primes-iterative that uses reduction.

   TODO: compare the performance of these two."
  [nth-prime]

  {:pre [(spec/assert ::valid-prime-fn-input nth-prime)]}

  (reduce (fn [accum cur]
            (if (= (dec nth-prime) (count accum))

              (reduced (cons (biginteger 2) (reverse accum)))

              ;; the isProbablePrime is non-deterministic...
              (check-against-primes (biginteger cur) accum)))
          '()
          (iterate #(+ 2 %) (biginteger 3))))
