(ns ordinance.facts
  "Municipal-ordinance compliance catalog for Shenzhen (深圳市) -- the third
  CHN member of the cloud-itonami-municipality-* compliance-fact family
  (ADR-2607141700), after -chn-beijing and -chn-shanghai. Gap recorded in
  superproject ADR-2607277000.

  THE POINT OF THIS PARTICULAR CITY: Shenzhen legislates under TWO distinct
  instruments, and the two entries below are deliberately one of each,
  because the difference is a real compliance fact and not a naming quirk.

  - 深圳经济特区控制吸烟条例 is a 经济特区法规 (Special Economic Zone
    regulation). Shenzhen has held SEZ legislative power since it became
    China's first SEZ in 1980, and such regulations take effect WITHOUT a
    provincial approval step.
  - 深圳市生活垃圾分类管理条例 is a 地方性法规 of a 较大的市, and its own
    announcement records the extra step verbatim: passed by the Shenzhen
    congress standing committee on 2019-12-31 and then 批准 by the GUANGDONG
    PROVINCIAL congress standing committee on 2020-06-23 before taking
    effect 2020-09-01. Beijing and Shanghai, as 直辖市, have no equivalent
    step -- their siblings' entries show none.

  Both were verified on 2026-07-27. Provenance differs between them and the
  difference is recorded rather than flattened:

  - The waste ordinance was read from cgj.sz.gov.cn (深圳市城市管理和综合
    执法局), a MUNICIPAL body's site: :official-sz-gov-cn.
  - The full verbatim header of the smoking ordinance was found on
    www.szns.gov.cn -- 深圳市南山区人民政府, a DISTRICT government inside
    Shenzhen, on its OWN domain. Note that `szns.gov.cn` is NOT a
    subdomain of `sz.gov.cn`; a test asserts that distinction, because an
    earlier draft of this repo casually described both citations as being
    'on sz.gov.cn' and that was simply false. It therefore carries the
    distinct provenance :official-shenzhen-district-gov-cn. The municipal
    卫生健康委员会 (wjw.sz.gov.cn) publishes a 政策解读 of the same 2019
    revision, which corroborates the revision but is an interpretation, not
    the text -- so it is NOT cited as the source here."
  (:require [kotoba.lang.text :as str]))

(def catalog
  "municipality-slug -> vector of ordinance entries."
  {"shenzhen"
   [{:ordinance/id "shenzhen.sez-kongzhi-xiyan-tiaoli-1998"
     :ordinance/title "深圳经济特区控制吸烟条例 (Shenzhen Special Economic Zone Regulations on Smoking Control)"
     :ordinance/municipality "shenzhen"
     :ordinance/country "CHN"
     :ordinance/kind :sez-regulation
     :ordinance/number "1998年8月28日深圳市第二届人民代表大会常务委员会第二十五次会议通过（2013年10月29日修订、2018年12月27日第一次修正、2019年6月26日第二次修正）"
     :ordinance/url "http://www.szns.gov.cn/nsqcgj/gkmlpt/content/7/7126/post_7126792.html"
     :ordinance/url-provenance :official-shenzhen-district-gov-cn
     :ordinance/enacted-date "1998-08-28"
     :ordinance/last-revised-date "2019-06-26"
     :ordinance/retrieved-at "2026-07-27"
     :ordinance/topic #{:public-health :smoking-control :e-cigarettes}}
    {:ordinance/id "shenzhen.shenghuo-laji-fenlei-guanli-tiaoli-2019"
     :ordinance/title "深圳市生活垃圾分类管理条例 (Shenzhen Municipal Regulations on Domestic Waste Classification Management)"
     :ordinance/municipality "shenzhen"
     :ordinance/country "CHN"
     :ordinance/kind :ordinance
     ;; The provincial approval and the 施行日 are both carried inside
     ;; :ordinance/number, because schema/ordinance.edn is deliberately
     ;; IDENTICAL across every municipality-* sibling -- that uniformity is
     ;; what lets the federated query join across them.
     :ordinance/number "深圳市第六届人民代表大会常务委员会公告第一九九号：2019年12月31日深圳市第六届人大常委会第三十七次会议通过，广东省第十三届人大常委会第二十一次会议2020年6月23日批准，自2020年9月1日起施行"
     :ordinance/url "http://cgj.sz.gov.cn/gkmlpt/content/7/7900/post_7900059.html"
     :ordinance/url-provenance :official-sz-gov-cn
     :ordinance/enacted-date "2019-12-31"
     :ordinance/retrieved-at "2026-07-27"
     :ordinance/topic #{:waste-management :environment :provincial-approval-required}}]})

(defn spec-basis [muni] (get catalog muni))

(defn coverage
  ([] (coverage (keys catalog)))
  ([munis]
   (let [have (filter catalog munis)
         missing (remove catalog munis)]
     {:requested (count munis)
      :covered (count have)
      :covered-municipalities (vec (sort have))
      :missing-municipalities (vec (sort missing))
      :note (str "cloud-itonami-municipality-chn-shenzhen (family ADR-2607141700, "
                 "gap recorded in ADR-2607277000): "
                 (count (get catalog "shenzhen")) " Shenzhen entries read "
                 "2026-07-27, deliberately ONE 经济特区法规 (SEZ regulation, "
                 "no provincial approval step) and ONE 地方性法规 whose own "
                 "announcement records GUANGDONG PROVINCIAL approval before "
                 "it could take effect. Provenance differs between them: the "
                 "smoking text was found only on a DISTRICT government site, "
                 "not the municipal portal, and carries a distinct "
                 ":url-provenance saying so. Extend "
                 "`ordinance.facts/catalog`, never fabricate an "
                 "id/url/number.")})))

(defn by-topic [muni topic]
  (filterv #(contains? (:ordinance/topic %) topic) (spec-basis muni)))

(defn sez-regulations
  "The entries enacted under Shenzhen's Special Economic Zone legislative
  power, which take effect without a provincial approval step -- as
  distinct from a 较大的市 地方性法规, which does not."
  [muni]
  (filterv #(= :sez-regulation (:ordinance/kind %)) (spec-basis muni)))

(defn provincially-approved
  "The entries whose own text records a provincial-congress approval step.
  Empty for a 直辖市 like Beijing or Shanghai; non-empty here."
  [muni]
  (filterv #(str/includes? (or (:ordinance/number %) "") "批准") (spec-basis muni)))
