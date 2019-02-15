(ns prime-numbers.algos.parallel
  "Find prime numbers with parallel methods."
  (:require
   [clojure.core.reducers :as r]
   [clojure.set           :as clj-set]
   [clojure.spec.alpha    :as spec])
  (:import

   ;; hypirion.com/musings/understanding-clojure-transients
   [clojure.lang PersistentHashSet]
   [clojure.lang PersistentList]
   [java.math    BigInteger]))


(declare primes-parallel-lazy)


;; Same as in iterative, could put this one in a shared ns...
(spec/check-asserts true)
(spec/def ::valid-prime-parallel-input pos-int?)


(defn- relatively-prime?
  "Checks that a number 'n' is relatively prime to all elements
   of the list 'primes-less-than-sqrt-n'.

   Returns n if it is relatively prime,
   a false-y (nil) value otherwise"
  [n primes-less-than-sqrt-n]
  (when (every? #(not= 0 (rem n %)) primes-less-than-sqrt-n) n))


(defn- psieve
  "Finds all relative-primes from a starting prime number up
   to its square.  Skips already found primes in this range.

   Outside of finding a better algorithm, this
   is where to look for optimization tweaks."
  [^BigInteger        cur-prime
   ^PersistentList    fully-sieved-primes
   ^PersistentHashSet larger-primes]
  (->> (iterate #(+ 2 %) (+ 2 cur-prime))

       ;; 'iterate' should give chunks that can be acted on in parallel.
       (r/take-while #(< % (* cur-prime cur-prime)))

       ;; Since this is a set it's also a fn.
       (r/remove larger-primes)

       ;; Check over each in parallel.
       (r/map #(relatively-prime? % fully-sieved-primes))

       ;; Don't care about the composite nums.
       (r/remove nil?)

       ;; Produce a set disjoint from 'larger-primes'.
       (into #{})))


(defn primes-parallel
  "Wraps the lazy seq below."
  [nth-prime]

  {:pre [(spec/assert ::valid-prime-parallel-input nth-prime)]}

  (cons (biginteger 2) (take (dec nth-prime) (primes-parallel-lazy))))


(defn- primes-parallel-lazy
  "Yields a lazy sequence"
  ([] (primes-parallel-lazy (biginteger 3) '() #{}))

  ;; The two collections being towed along are a complete list of primes
  ;; that are <= 'cur-prime' as well as an incomplete set of primes that
  ;; lie between cur-prime and cur-prime^2 this 'partially-sieved' set.
  ([^BigInteger        cur-prime
    ^PersistentList    fully-sieved-primes
    ^PersistentHashSet partially-sieved-primes]

   ;; At each value yielded we parallelize the task of finding
   ;; the next prime. While doing this we also:
   ;;    i) perform the parallel sieveing.
   ;;   ii) merge this result in with prior partially-sieved primes
   ;;  iii) bind to this new set, and also its minimum.
   ;;   iv) cons the current prime onto our lazy sequence
   ;;    v) continue calling 'primes-parallel-lazy'.
   (let [[new-psp next-prime] ((juxt identity #(apply min %))
                               (clj-set/union
                                partially-sieved-primes
                                (psieve cur-prime
                                        fully-sieved-primes
                                        partially-sieved-primes)))]
     (lazy-seq
      (cons

       ;; cur-prime is provably the next consecutive prime.
       cur-prime

       ;; continue lazily yielding...
       (primes-parallel-lazy (biginteger next-prime)
                             (cons cur-prime fully-sieved-primes)
                             (disj new-psp next-prime)))))))
