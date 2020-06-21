;   Copyright (c) 2020 International Institute for Applied Systems Analysis.
;   All rights reserved. The use and distribution terms for this software
;   are covered by the MIT License (http://opensource.org/licenses/MIT)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns commodities-auction.compute.rule-test
  (:require [clojure.test :refer :all]
            [commodities-auction.compute.rule :refer :all]))

;;; tests

(deftest adjust-prices-test
  (testing "Run English auction for differentiated commodity markets"
    (; Act
     let [result1 (adjust-prices {:c1 5 :c2 7 :c3 4}
                                 {:m1 {:demand 9
                                       :entry {:c1 0 :c2 0 :c3 1}}
                                  :m2 {:demand 11
                                       :entry {:c1 0 :c2 1 :c3 0}}}
                                 20)
          result2 (adjust-prices {:c1 5 :c2 7 :c3 4}
                                 {:m1 {:demand 9
                                       :entry {:c1 2 :c2 4 :c3 5}}
                                  :m2 {:demand 11
                                       :entry {:c1 2 :c2 5 :c3 4}}}
                                 20)
          result3 (adjust-prices {:c1 3 :c2 4 :c3 1}
                                 {:m1 {:demand 9
                                       :entry {:c1 2 :c2 1 :c3 3}}
                                  :m2 {:demand 11
                                       :entry {:c1 1 :c2 1 :c3 2}}}
                                 20)
          result4 (adjust-prices {:c1 5 :c2 4 :c3 7}
                                 {:m1 {:demand 9
                                       :entry {:c1 4 :c2 9 :c3 5}}
                                  :m2 {:demand 11
                                       :entry {:c1 5 :c2 2 :c3 7}}}
                                 20)
          result5 (adjust-prices {:c1 4 :c2 2 :c3 5 :c4 3}
                                 {:m1 {:demand 7
                                       :entry {:c1 11 :c2 13 :c3 12 :c4 11}}
                                  :m2 {:demand 4
                                       :entry {:c1 12 :c2 14}}
                                  :m3 {:demand 6
                                       :entry {:c1 10 :c2 11 :c3 14 :c4 13}}}
                                 17)
          result6 (adjust-prices {:c1 3 :c2 4 :c3 1}
                                 {:m1 {:demand 11
                                       :entry {:c1 1 :c2 0 :c3 2}}
                                  :m2 {:demand 9
                                       :entry {:c1 2 :c2 1 :c3 3}}}
                                 20)
          result7 (adjust-prices {:c1 5 :c2 5 :c3 3}
                                 {:m1 {:demand 5
                                       :entry {:c1 1 :c2 1 :c3 6}}
                                  :m2 {:demand 6
                                       :entry {:c1 0 :c2 0 :c3 4}}}
                                 11)
          result8 (adjust-prices {:c1 4 :c2 5 :c3 2}
                                 {:m1 {:demand 7
                                       :entry {:c1 0 :c2 0 :c3 1}}
                                  :m2 {:demand 6
                                       :entry {:c1 1 :c2 1 :c3 1}}}
                                 13)]

      ; Assert
      (is (= result1
             {:imports {:c1 [0 1 7 8 9]
                        :c2 [0 0 6 6 7]
                        :c3 [0 1 7 8 9]}
              :markets {:m1 [4 4 10 11 12]
                        :m2 [4 5 11 11 12]}}))

      (is (= result2
             {:imports {:c1 [0 1 2 3 4 6 7]
                        :c2 [0 0 0 1 1 3 3]
                        :c3 [0 1 1 2 3 5 5]}
              :markets {:m1 [7 8 8 9  10 12 12]
                        :m2 [8 8 9 10 10 12 12]}}))

      (is (= result3
             {:imports {:c1 [0 13]
                        :c2 [0 13]
                        :c3 [0 13]}
              :markets {:m1 [12 17]
                        :m2 [12 16]}}))

      (is (= result4
             {:imports {:c1 [0 1 2 3 4]
                        :c2 [0 0 1 1 2]
                        :c3 [0 0 1 1 2]}
              :markets {:m1 [9  10 11 11 12]
                        :m2 [10 10 11 11 12]}}))

      (is (= result5
             {:imports {:c1 [0 1 2]
                        :c2 [0 0 1]
                        :c3 [0 0 0]
                        :c4 [0 0 0]}
              :markets {:m1 [13 13 14]
                        :m2 [15 15 16]
                        :m3 [13 13 14]}}))

      (is (= result6
             {:imports {:c1 [0 13]
                        :c2 [0 13]
                        :c3 [0 13]}
              :markets {:m1 [12 16]
                        :m2 [12 17]}}))

      (is (= result7
             {:imports {:c1 [0 2] :c2 [0 2] :c3 [0 0]}
              :markets {:m1 [4 6] :m2 [3 5]}}))

      (is (= result8
             {:imports {:c1 [0 1 4 4]
                        :c2 [0 0 3 4]
                        :c3 [0 1 4 5]}
              :markets {:m1 [3 4 7 7] :m2 [3 4 7 8]}})))))
