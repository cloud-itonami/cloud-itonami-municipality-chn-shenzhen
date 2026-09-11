(ns culture.facts-test
  (:require [clojure.edn :as edn]
            [kotoba.lang.text :as str]
            [clojure.test :refer [deftest is testing]]
            [culture.facts :as facts]))

(deftest shenzhen-has-culture-basis
  (let [sb (facts/spec-basis "shenzhen")]
    (is (= 3 (count sb)))
    (is (= (count sb) (count (set (map :culture/id sb)))) "ids are unique")
    (is (every? #(str/starts-with? (:culture/url %) "https://") sb))
    (is (every? #(= "shenzhen" (:culture/municipality %)) sb))
    (is (every? #(= "CHN" (:culture/country %)) sb))
    (is (every? #(seq (:culture/summary %)) sb))
    (is (every? #(= "2026-07-27" (:culture/retrieved-at %)) sb))))

(deftest unknown-municipality-has-no-basis
  (is (nil? (facts/spec-basis "guangzhou")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["shenzhen" "guangzhou"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["guangzhou"] (:missing-municipalities c)))))

(deftest the-catalog-is-small-on-purpose-and-says-so
  (testing "a 45-year-old city has no verified stock of dishes/festivals/crafts, and nothing was padded in"
    (is (empty? (facts/by-kind "shenzhen" :dish)))
    (is (empty? (facts/by-kind "shenzhen" :festival)))
    (is (empty? (facts/by-kind "shenzhen" :craft))))
  (testing "and the coverage note records WHY each candidate was dropped"
    (let [note (:note (facts/coverage))]
      (is (str/includes? note "DELIBERATELY"))
      (is (str/includes? note "Hakka") "the source does not mention Shenzhen")
      (is (str/includes? note "404") "the 盆菜 URL tried was not readable"))))

(deftest by-kind-filters
  (is (= 2 (count (facts/by-kind "shenzhen" :heritage))))
  (is (= ["shenzhen.civic-history.first-special-economic-zone"]
         (mapv :culture/id (facts/by-kind "shenzhen" :civic-history))))
  (is (empty? (facts/by-kind "guangzhou" :heritage))))

(deftest both-walled-towns-predate-the-city-by-six-centuries
  (let [towns (facts/ming-era-walled-towns "shenzhen")]
    (is (= 2 (count towns)))
    (is (= #{"shenzhen.heritage.dapeng-fortress" "shenzhen.heritage.nantou"}
           (set (map :culture/id towns))))
    (testing "while the SEZ designation that created the modern city is 1980"
      (is (str/includes?
           (:culture/summary (first (facts/by-kind "shenzhen" :civic-history)))
           "May 1980")))))

(deftest nantou-does-not-claim-a-designation-its-source-did-not-state
  (let [nantou (first (filter #(= "shenzhen.heritage.nantou" (:culture/id %))
                              (facts/spec-basis "shenzhen")))]
    (is (str/includes? (:culture/summary nantou) "states no formal heritage designation"))
    (testing "whereas Dapeng, whose source DID state designations, records both"
      (let [dapeng (first (filter #(= "shenzhen.heritage.dapeng-fortress" (:culture/id %))
                                  (facts/spec-basis "shenzhen")))]
        (is (str/includes? (:culture/summary dapeng) "1983"))
        (is (str/includes? (:culture/summary dapeng) "1989"))))))

(deftest tx-file-matches-catalog
  (let [tx (edn/read-string (slurp "data/culture-tx.edn"))
        flat (mapcat val (sort-by key facts/catalog))]
    (is (= (vec flat) (vec tx)))))

(deftest every-attribute-used-is-declared-in-the-schema
  (let [declared (set (keys (edn/read-string (slurp "schema/culture.edn"))))
        used (set (mapcat keys (mapcat val facts/catalog)))]
    (is (empty? (remove declared used))
        (str "undeclared: " (vec (remove declared used))))))
