(defproject prime_numbers "0.0.1-alpha"
  :description "A simple app that generates prime numbers."
  :url "http://github.com/zerjens/prime_numbers"
  
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.0"]]

  :plugins      [[lein-ancient    "0.6.15"]
                 [lein-kibit      "0.1.6"]
                 [lein-marginalia "0.9.1"]
                 [venantius/yagni "0.1.7"]]

  :aot :all

  :profiles {:uberjar {:aot :all}}

  :main prime-numbers.main

  :target-path "target/"

  :repl-options {:init-ns prime-numbers.main})
