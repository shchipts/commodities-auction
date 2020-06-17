;   Copyright (c) 2020 International Institute for Applied Systems Analysis.
;   All rights reserved. The use and distribution terms for this software
;   are covered by the MIT License (http://opensource.org/licenses/MIT)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "Generalized English auction for differentiated commodity markets."
      :author "Anna Shchiptsova"}
 commodities-auction.auction
  (:require [clojure.math.numeric-tower :as math]
            [commodities-auction.compute.rule :as rule]))

(defn- aggregate
  "Returns auction results as import prices, market prices and surpluses."
  [{imports :imports markets :markets}]
  (->> (vals markets)
       (map (juxt last
                  #(- (last %)
                      (first %))))
       (apply map vector)
       (map #(zipmap (keys markets) %))
       ((fn [coll]
          (conj coll
                (reduce-kv #(assoc %1 %2 (last %3))
                           {}
                           imports))))
       (zipmap [:imports :markets :surplus])))

(defn- transform
  "Returns markets' parameters with entry prices scaled to
  total market size."
  [demand entry scale]
  (reduce-kv (fn [m k v]
               (->> (get entry k)
                    ((juxt keys
                           (fn [coll]
                             (map #(math/round (* % scale))
                                  (vals coll)))))
                    (apply zipmap)
                    (vector v)
                    (zipmap [:demand :entry])
                    (assoc m k)))
             {}
             demand))

(defn- invert
  "Returns relative prices."
  [prices scale]
  (->> (vals prices)
       (map (fn [m]
              (->> (vals m)
                   (map #(map (comp double /)
                              %
                              (repeat scale)))
                   (zipmap (keys m)))))
       (zipmap (keys prices))))

(defn run
  "Finds equilibrium prices for differentiated commodity markets.
  The algorithm will run the generalized English auction (Gul &
  Stacchetti 2000) to balance interaction between different markets
  concurrently utilizing the same import resources."
  ([supply demand entry]
   (run supply demand entry nil))
  ([supply demand entry option]
   (->> (vals demand)
        (apply +)
        ((fn [scale]
           (-> (transform demand entry scale)
               (#(rule/adjust-prices supply
                                     %
                                     scale))
               (invert scale))))
        (#(case option
            :summary (aggregate %)
            %)))))
