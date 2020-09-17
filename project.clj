(defproject org.iiasa/commodities-auction "1.0.0"
  :description "A special case of generalized English auction."
  :url "https://github.com/shchipts/commodities-auction"
  :scm {:name "git"
        :url "https://github.com/shchipts/commodities-auction"}
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/math.combinatorics "0.1.6"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.iiasa/utilities-clj "1.0.0"]]
  :plugins [[lein-codox "0.9.5"]]
  :codox {:output-path "docs"}
  :deploy-repositories [["clojars" {:url "https://repo.clojars.org"
									:username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false}]]
  :main nil)
