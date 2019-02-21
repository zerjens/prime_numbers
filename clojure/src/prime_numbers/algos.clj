(ns prime-numbers.algos
  "A few recipes for generating prime numbers."
  (:require
   [clojure.core.reducers :as r]
   [clojure.spec.alpha    :as spec]))



;;;; Perf Check - want to avoid reflection wherever possible.

(set! *warn-on-reflection* true)



;;;; Specs

;; Ensures we throw an ExceptionInfo when a spec isn't valid. Overrides
;; runtime args for enable-assertions, evaled when namespace is loaded.
(spec/check-asserts true)

;; This is intended to be a compositional style function, where
;; all other prime-finding functions are composed of this spec:
;; Ns qualified spec, applied in ':pre' section of relevant fns.
(spec/def ::prime-fn-spec pos-int?)



;;;; Basic Protocol/Type defs

(defprotocol PrimeNumbers
  "General interface for prime algorithms in this namespace."
  (n-primes [this n] "return a sequence of 'n' prime numbers"))

(deftype Primes [algo]
  PrimeNumbers
  (n-primes [this n]
    "Spec for valid input range, then apply prime number algo."
    {:pre [(spec/assert ::prime-fn-spec n)]}
    (algo n)))


;;;; Sequential Implementation

(defn- next-primes-list
  "For every odd number 'cur' encountered, check that it
   is relatively prime to an accumulated list of primes."
  [^BigInteger cur primes]
  (let [sqrt-cur (inc (.sqrt cur))
        prime?    (->> primes
                       (drop-while #(< sqrt-cur %))
                       (every?     #(not= 0 (rem cur %))))]
    (if-not prime? primes (cons cur primes))))

;;;; Looping implementation

(defn primes-loop
  "Simple looping impl"
  [n]
  (loop [primes '()
         [^BigInteger cur & tl] (iterate (partial + 2) (biginteger 3))]

    (if (= (count primes) (dec n))

      ;; accumulator cons'd primes in ascending order, so reverse.
      (cons (biginteger 2) (reverse primes))

      ;; pass in potentially updated list of primes & next odd # in.
      ;;
      ;; An interesting error when just type hinting 'cur' happens here.
      ;; Need to look into this further...
      ;;
      ;; Execution error (ClassCastException) at
      ;; prime-numbers.algos/next-primes-list$fn (algos.clj:43).
      ;; (clojure.lang.BigInt is in unnamed module of loader 'app';
      ;;  java.math.BigInteger is in module java.base of loader 'bootstrap')
      (recur (next-primes-list (biginteger cur) primes) tl))))



;;;; Reduce implementation

(defn- primes-reduce
  "Similar to the looping example above execept that the
   accumulation of primes is done through reduction/folding"
  [n]
  (reduce (fn [primes ^BigInteger cur]

            (if (= (count primes) (dec n))

              (reduced (cons (biginteger 2) (reverse primes)))

              (next-primes-list (biginteger cur) primes)))

          '()

          (iterate #(+ 2 %) (biginteger 3))))



;;;; Y-Combinator implementation

(defn- Y
  "Clojure Combinator (combinators have no free variables
   and in practice take a generating fn).  In Lambda Calc:
   > λf. (λx. f (x x))(λx. f (x x))

   which we can make look more like LISP as:
   > (λ (f)
      ((λ (x) (f (x x)))
       (λ (x) (f (x x)))))

   this is reducible to:
   > (λ (f)
      ((λ (x) (x x))
       (λ (x) (f (x x)))))


   TODO: futher dervivation."
  [f]
  ((fn [x] (x x))               ; first dispatch
   (fn [y] (f (fn [p]           ; unravels remaining 'recusive' calls
                ((y y) p))))))

;; TODO: use accumulated list to derive nth prime in sequence,
;;       as opposed to relying on earlier prime finding algos.
(defn- primes-generator [func]
  (fn [[h & t]]
    (when h (cons (last (primes-loop h)) (func t)))))

(defn- primes-y-comb
  "Putting the generating function in here so it has closure on 'n'"
  [n]
  ((Y primes-generator) (range 1 (inc n))))



;;;; Lazy-Seq implementation; this needs as much work as the Y comb.

(defn- prime-numbers-lazy
  ([]  (prime-numbers-lazy 1))
  ([n] (lazy-seq
        (cons
         (last (primes-loop n))
         (prime-numbers-lazy (inc n))))))

(defn- primes-lazy
  ""
  [n]
  (take n (prime-numbers-lazy)))
