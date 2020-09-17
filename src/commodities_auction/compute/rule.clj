;   Copyright (c) 2020 International Institute for Applied Systems Analysis.
;   All rights reserved. The use and distribution terms for this software
;   are covered by the MIT License (http://opensource.org/licenses/MIT)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "Price adjustment process for differentiated commodity markets."
      :author "Anna Shchiptsova"}
 commodities-auction.compute.rule
  (:require [commodities-auction.compute.demand-correspondence :as dp]
            [commodities-auction.compute.market-functions :as mf]))

(defn- drop-while-with-counter
  "Mimics clojure.core/drop-while with the counter + 1 for dropped items.
  NB: pred must return false for some item in coll."
  ([pred coll]
   (let [step (fn [pred coll counter]
                (let [s (seq coll)]
                  (if (pred (first s))
                    (recur pred (rest s) (inc counter))
                    (list counter s))))]
     ((juxt first
            #(lazy-seq (second %)))
      (step pred coll 1)))))

(defn- add-record
  "Appends current prices to accumulated results."
  [prev prices demand-sets]
  (->> (map :price demand-sets)
       (conj (second prev))
       (vector (conj (first prev) prices))))

(defn- record
  "Appends iteration prices to accumulated results
  and accumulates sub-iteration results for further recording."
  ([] [[] []])
  ([prev supply demand-sets incs]
   (let [export (first prev)
         x (map #(if (nil? (get incs %))
                   0 (get incs %))
                (keys supply))
         old? (and (> (count export) 1)
                   (->> (last export)
                        (vector x)
                        (map #(keep pos? %))
                        (apply =)))]
     (if old?
       (add-record (map pop prev)
                   (map + x (last export))
                   demand-sets)
       (add-record prev x demand-sets)))))

(defn- iteration
  "Returns auction state."
  [supply]
  (fn compile-iteration
    ([markets]
     (let [demand-sets (map dp/build markets)]
       (compile-iteration markets
                          (record)
                          {}
                          demand-sets)))
    ([markets
      iterations
      increments
      demand-sets]
     (hash-map :demand-sets
               demand-sets
               :markets
               markets
               :excess-demand
               (mf/excess-demand-set demand-sets supply)
               :iterations
               (record iterations
                       supply
                       demand-sets
                       increments)))))

(defn- market-balance
  "Finds market prices for supply and demand after price increase
  for imports in excess demand set."
  [{prev-demand-sets :demand-sets
    prev-markets :markets
    excess-demand :excess-demand
    iterations :iterations}
   iteratef]
  (let [increments (->> (map #(dp/price-inc excess-demand %1 %2)
                             prev-markets
                             prev-demand-sets)
                        (filter some?)
                        (reduce (fn [s x] (if (< x s) x s)))
                        repeat
                        (zipmap excess-demand))]
    (->> (map (fn [pars]
                (update pars
                        :entry
                        #(merge-with + % (select-keys increments (keys %)))))
              prev-markets)
         ((juxt identity
                #(vec (map dp/rebuild % prev-demand-sets))))
         ((fn [[cur-markets cur-demand-sets]]
            (iteratef cur-markets
                      iterations
                      increments
                      cur-demand-sets))))))

(defn- transpose
  "Returns results of every iteration as supplier prices and market prices."
  [supply-ids market-ids iterations]
  (zipmap [:suppliers :markets]
          ((juxt #(% (first iterations)
                     supply-ids
                     (fn [v1 v2]
                       ((fnil (fn [x] (+ x v2)) 0) v1)))
                 #(% (second iterations)
                     market-ids
                     (fn [_ v2] (identity v2))))
           (fn [coll ks f]
             (zipmap ks
                     (apply map
                            (fn [& more]
                              (persistent!
                               (reduce (fn [x y]
                                         (->> (count x)
                                              dec
                                              (get x)
                                              (#(f % y))
                                              (conj! x)))
                                       (transient [])
                                       more)))
                            coll))))))

(defn adjust-prices
  "A price adjustment procedure to find equilibrium prices for differentiated
  commodity markets.

  Finds a minimal Walrasian equilibrium using the procedure of generalized
  English auction (Gul & Stacchetti 2000). Export flows are constrained by
  capacity in `supply` and are differentiated by floor prices for market entry
  in `markets` :entry. The individual market demand in `markets` :demand is
  assumed to be inelastic. Import goods can be substituted with domestic
  production at higher market prices. International suppliers and domestic
  industry players alter their supply volumes identically in response to the
  changes in market price. `price-scale` is an upper bound on market price
  corresponding to the case of only domestic supply. Returns a hash map with
  supplier prices (:suppliers) and market prices (:markets) for each auction
  iteration."
  [supply markets price-scale]
  (let [iteratef (iteration supply)]
    (->> (vals markets)
         (map (fn [s x m]
                (->> (:entry m)
                     keys
                     (select-keys s)
                     (#(assoc m :supply % :total-demand x))))
              (repeat supply)
              (repeat price-scale))
         iteratef
         (iterate #(market-balance % iteratef))
         (drop-while-with-counter #(seq (:excess-demand %)))
         ((juxt (comp :iterations first second)
                first))
         ((fn [[v n]]
            (-> (keys supply)
                (transpose (keys markets)
                           v)
                (assoc :sub-iterations n)))))))
