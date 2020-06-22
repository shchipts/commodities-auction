;   Copyright (c) 2020 International Institute for Applied Systems Analysis.
;   All rights reserved. The use and distribution terms for this software
;   are covered by the MIT License (http://opensource.org/licenses/MIT)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns commodities-auction.auction-test
  (:require [clojure.test :refer :all]
            [commodities-auction.auction :refer :all]
            [utilities-clj.floating-point-comparison :refer :all]))

;;; tests

(deftest iterations
  (testing "with outcomes for every iteration"
    (; Act
     let [auction1 (run {:c1 5 :c2 7 :c3 4}
                        {:m1 9 :m2 11}
                        {:m1 {:c1 0 :c2 0 :c3 0.05}
                         :m2 {:c1 0 :c2 0.05 :c3 0}})
          auction2 (run {:c1 4 :c2 2 :c3 5 :c4 3}
                        {:m1 7 :m2 4 :m3 6}
                        {:m1 {:c1 0.647059 :c2 0.764706
                              :c3 0.705882 :c4 0.647059}
                         :m2 {:c1 0.705882 :c2 0.823529
                              :c3 1 :c4 1}
                         :m3 {:c1 0.588235 :c2 0.647059
                              :c3 0.823529 :c4 0.764706}})]

      ; Assert
      (is (real= (get-in auction1 [:imports :c1])
                 [0 0.05 0.35	0.4	0.45]))
      (is (real= (get-in auction1 [:imports :c2])
                 [0	0	0.3 0.3 0.35]))
      (is (real= (get-in auction1 [:imports :c3])
                 [0 0.05 0.35	0.4	0.45]))
      (is (real= (get-in auction1 [:markets :m1])
                 [0.2	0.2	0.5 0.55	0.6]))
      (is (real= (get-in auction1 [:markets :m2])
                 [0.2	0.25 0.55	0.55 0.6]))
      (is (= (:sub-iterations auction1)
             6))

      (is (real= (get-in auction2 [:imports :c1])
                 [0 0.058823529	0.117647059]))
      (is (real= (get-in auction2 [:imports :c2])
                 [0 0 0.058823529]))
      (is (real= (get-in auction2 [:imports :c3])
                 [0 0 0]))
      (is (real= (get-in auction2 [:imports :c4])
                 [0 0 0]))
      (is (real= (get-in auction2 [:markets :m1])
                 [0.764705882	0.764705882	0.823529412]))
      (is (real= (get-in auction2 [:markets :m2])
                 [0.882352941 0.882352941 0.941176471]))
      (is (real= (get-in auction2 [:markets :m3])
                 [0.764705882	0.764705882	0.823529412]))
      (is (= (:sub-iterations auction2)
             3)))))

(deftest surpluses
  (testing "with surpluses as outcomes"
    (; Act
     let [auction1 (run {:c1 5 :c2 7 :c3 4}
                        {:m1 9 :m2 11}
                        {:m1 {:c1 0 :c2 0 :c3 0.05}
                         :m2 {:c1 0 :c2 0.05 :c3 0}}
                        :summary)
          auction2 (run {:c1 5 :c2 7 :c3 4}
                        {:m1 9 :m2 11}
                        {:m1 {:c1 0.1 :c2 0.2 :c3 0.25}
                         :m2 {:c1 0.1 :c2 0.25 :c3 0.2}}
                        :summary)
          auction3 (run {:c1 3 :c2 4 :c3 1}
                        {:m1 9 :m2 11}
                        {:m1 {:c1 0.1 :c2 0.05 :c3 0.15}
                         :m2 {:c1 0.05 :c2 0.05 :c3 0.1}}
                        :summary)
          auction4 (run {:c1 5 :c2 4 :c3 7}
                        {:m1 9 :m2 11}
                        {:m1 {:c1 0.2 :c2 0.45 :c3 0.25}
                         :m2 {:c1 0.25 :c2 0.1 :c3 0.35}}
                        :summary)

          auction5 (run {:c1 4 :c2 2 :c3 5 :c4 3}
                        {:m1 7 :m2 4 :m3 6}
                        {:m1 {:c1 0.647058824 :c2 0.764705882
                              :c3 0.705882353 :c4 0.647058824}
                         :m2 {:c1 0.705882353 :c2 0.823529412
                              :c3 1 :c4 1}
                         :m3 {:c1 0.588235294 :c2 0.647058824
                              :c3 0.823529412 :c4 0.764705882}}
                        :summary)]

      ; Assert
      (is (real= (get-in auction1 [:imports :c1])
                 0.45))
      (is (real= (get-in auction1 [:imports :c2])
                 0.35))
      (is (real= (get-in auction1 [:imports :c3])
                 0.45))
      (is (real= (get-in auction1 [:markets :m1])
                 0.6))
      (is (real= (get-in auction1 [:markets :m2])
                 0.6))
      (is (real= (get-in auction1 [:surplus :m1])
                 0.4))
      (is (real= (get-in auction1 [:surplus :m2])
                 0.4))
      (is (= (:sub-iterations auction1)
             6))

      (is (real= (get-in auction2 [:imports :c1])
                 0.35))
      (is (real= (get-in auction2 [:imports :c2])
                 0.15))
      (is (real= (get-in auction2 [:imports :c3])
                 0.25))
      (is (real= (get-in auction2 [:markets :m1])
                 0.6))
      (is (real= (get-in auction2 [:markets :m2])
                 0.6))
      (is (real= (get-in auction2 [:surplus :m1])
                 0.25))
      (is (real= (get-in auction2 [:surplus :m2])
                 0.2))
      (is (= (:sub-iterations auction2)
             8))

      (is (real= (get-in auction3 [:imports :c1])
                 0.65))
      (is (real= (get-in auction3 [:imports :c2])
                 0.65))
      (is (real= (get-in auction3 [:imports :c3])
                 0.65))
      (is (real= (get-in auction3 [:markets :m1])
                 0.85))
      (is (real= (get-in auction3 [:markets :m2])
                 0.8))
      (is (real= (get-in auction3 [:surplus :m1])
                 0.25))
      (is (real= (get-in auction3 [:surplus :m2])
                 0.2))
      (is (= (:sub-iterations auction3)
             8))

      (is (real= (get-in auction4 [:imports :c1])
                 0.2))
      (is (real= (get-in auction4 [:imports :c2])
                 0.1))
      (is (real= (get-in auction4 [:imports :c3])
                 0.1))
      (is (real= (get-in auction4 [:markets :m1])
                 0.6))
      (is (real= (get-in auction4 [:markets :m2])
                 0.6))
      (is (real= (get-in auction4 [:surplus :m1])
                 0.15))
      (is (real= (get-in auction4 [:surplus :m2])
                 0.1))
      (is (= (:sub-iterations auction4)
             5))

      (is (real= (get-in auction5 [:imports :c1])
                 0.117647059))
      (is (real= (get-in auction5 [:imports :c2])
                 0.058823529))
      (is (real= (get-in auction5 [:imports :c3])
                 0))
      (is (real= (get-in auction5 [:imports :c4])
                 0))
      (is (real= (get-in auction5 [:markets :m1])
                 0.823529412))
      (is (real= (get-in auction5 [:markets :m2])
                 0.941176471))
      (is (real= (get-in auction5 [:markets :m3])
                 0.823529412))
      (is (real= (get-in auction5 [:surplus :m1])
                 0.058823529))
      (is (real= (get-in auction5 [:surplus :m2])
                 0.058823529))
      (is (real= (get-in auction5 [:surplus :m3])
                 0.058823529))
      (is (= (:sub-iterations auction5)
             3)))))

(deftest no-imports
  (testing "with non-competitive imports"
    (; Act
     let [auction (run {:c1 4 :c2 2 :c3 5 :c4 3}
                       {:m1 7 :m2 4 :m3 6}
                       {:m1 {:c1 0.529411765 :c2 0.470588235
                             :c3 0.705882353 :c4 0.647058824}
                        :m2 {:c1 0.941176471 :c2 0.882352941}
                        :m3 {:c1 0.352941176 :c2 0.411764706
                             :c3 0.823529412 :c4 0.764705882}}
                       :summary)]

      ; Assert
      (is (real= (get-in auction [:imports :c1])
                 0.176470588))
      (is (real= (get-in auction [:imports :c2])
                 0.235294118))
      (is (real= (get-in auction [:imports :c3])
                 0))
      (is (real= (get-in auction [:imports :c4])
                 0))
      (is (real= (get-in auction [:markets :m1])
                 0.764705882))
      (is (real= (get-in auction [:markets :m2])
                 1))
      (is (real= (get-in auction [:markets :m3])
                 0.764705882))
      (is (real= (get-in auction [:surplus :m1])
                 0.058823529))
      (is (real= (get-in auction [:surplus :m2])
                 0.058823529))
      (is (real= (get-in auction [:surplus :m3])
                 0.117647059)))))

;;; test grouping


(deftest run-test
  (testing "Run English auction:\n"
    (iterations)
    (surpluses)
    (no-imports)))


;;; tests in the namespace


(defn test-ns-hook
  "Explicit definition of tests in the namespace."
  []
  (run-test))
