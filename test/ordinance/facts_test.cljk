(ns ordinance.facts-test
  (:require [clojure.edn :as edn]
            [kotoba.lang.text :as str]
            [clojure.test :refer [deftest is testing]]
            [ordinance.facts :as facts]))

(deftest shenzhen-has-spec-basis
  (let [sb (facts/spec-basis "shenzhen")]
    (is (= 2 (count sb)))
    (is (every? #(str/includes? (:ordinance/url %) ".gov.cn") sb)
        "both citations are on an official Chinese government host")
    (testing "but on DIFFERENT hosts, and szns.gov.cn is a separate domain — NOT a sz.gov.cn subdomain"
      (is (str/includes? (:ordinance/url (first (facts/by-topic "shenzhen" :waste-management)))
                         "cgj.sz.gov.cn"))
      (is (str/includes? (:ordinance/url (first (facts/sez-regulations "shenzhen")))
                         "www.szns.gov.cn"))
      (is (not (str/includes? (:ordinance/url (first (facts/sez-regulations "shenzhen")))
                              ".sz.gov.cn"))
          "szns.gov.cn is Nanshan District's own domain; calling it a Shenzhen-municipal host would be wrong"))
    (is (every? #(= "CHN" (:ordinance/country %)) sb))
    (is (every? #(= "2026-07-27" (:ordinance/retrieved-at %)) sb))))

(deftest unknown-municipality-has-no-spec-basis
  (is (nil? (facts/spec-basis "beijing")))
  (is (nil? (facts/spec-basis "shanghai")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["shenzhen" "guangzhou"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["guangzhou"] (:missing-municipalities c)))))

;; ---- the reason this city is interesting: two different instruments ----

(deftest the-two-entries-are-one-sez-regulation-and-one-provincially-approved-ordinance
  (testing "Shenzhen legislates under BOTH instruments and the catalog holds one of each"
    (is (= 1 (count (facts/sez-regulations "shenzhen"))))
    (is (= 1 (count (facts/provincially-approved "shenzhen"))))
    (is (empty? (filter #(= :sez-regulation (:ordinance/kind %))
                        (facts/provincially-approved "shenzhen")))
        "the SEZ regulation is NOT the provincially-approved one -- that is the whole distinction")))

(deftest the-sez-regulation-records-no-provincial-approval
  (let [sez (first (facts/sez-regulations "shenzhen"))]
    (is (str/starts-with? (:ordinance/title sez) "深圳经济特区"))
    (is (not (str/includes? (:ordinance/number sez) "批准"))
        "SEZ regulations take effect without a provincial approval step")
    (is (= "1998-08-28" (:ordinance/enacted-date sez)))
    (is (= "2019-06-26" (:ordinance/last-revised-date sez))
        "the SECOND 修正, not the 2013 修订 or the 2018 first 修正")
    (is (contains? (:ordinance/topic sez) :e-cigarettes))))

(deftest the-municipal-ordinance-records-the-guangdong-approval-step-verbatim
  (let [waste (first (facts/by-topic "shenzhen" :waste-management))]
    (is (= :ordinance (:ordinance/kind waste)))
    (is (str/includes? (:ordinance/number waste) "广东省第十三届人大常委会")
        "the provincial approving body, from the announcement text")
    (is (str/includes? (:ordinance/number waste) "2020年6月23日批准"))
    (is (str/includes? (:ordinance/number waste) "2020年9月1日起施行"))
    (is (= "2019-12-31" (:ordinance/enacted-date waste))
        "the municipal passage date, which precedes provincial approval by ~6 months")
    (is (contains? (:ordinance/topic waste) :provincial-approval-required))))

;; ---- provenance honesty ----

(deftest the-smoking-text-declares-that-it-came-from-a-district-not-municipal-site
  (testing "the full verbatim header was found only on a district government site; that is recorded, not flattened"
    (let [sez (first (facts/sez-regulations "shenzhen"))
          waste (first (facts/by-topic "shenzhen" :waste-management))]
      (is (= :official-shenzhen-district-gov-cn (:ordinance/url-provenance sez)))
      (is (= :official-sz-gov-cn (:ordinance/url-provenance waste)))
      (is (not= (:ordinance/url-provenance sez) (:ordinance/url-provenance waste))
          "the two provenances are genuinely different values, not a copy-paste")))
  (is (str/includes? (:note (facts/coverage)) "DISTRICT government site")))

(deftest by-topic-filters
  (is (= ["shenzhen.sez-kongzhi-xiyan-tiaoli-1998"]
         (mapv :ordinance/id (facts/by-topic "shenzhen" :public-health))))
  (is (empty? (facts/by-topic "shenzhen" :housing)))
  (is (empty? (facts/by-topic "guangzhou" :waste-management))))

(deftest tx-file-matches-catalog
  (let [tx (edn/read-string (slurp "data/datascript-tx.edn"))
        flat (mapcat val (sort-by key facts/catalog))]
    (is (= (vec flat) (vec tx)))))

(deftest every-attribute-used-is-declared-in-the-schema
  (testing "schema/ordinance.edn is deliberately IDENTICAL across every municipality-* sibling"
    (let [declared (set (keys (edn/read-string (slurp "schema/ordinance.edn"))))
          used (set (mapcat keys (mapcat val facts/catalog)))]
      (is (empty? (remove declared used))
          (str "undeclared: " (vec (remove declared used)))))))
