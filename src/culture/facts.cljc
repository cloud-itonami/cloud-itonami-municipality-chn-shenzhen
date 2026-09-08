(ns culture.facts
  "Regional-culture catalog for Shenzhen (深圳市), sibling namespace to
  `ordinance.facts` per ADR-2607171400.

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms.

  THIS CATALOG IS DELIBERATELY SMALL, AND THAT IS THE FINDING. Shenzhen
  became China's first Special Economic Zone in May 1980; the Beijing and
  Kyoto siblings each carry 8-9 entries drawn from centuries of dishes,
  festivals and crafts, and Shenzhen does not have an equivalent stock of
  verifiable municipal-scoped items. Two candidates were investigated and
  DROPPED rather than padded in:

  - Hakka walled villages: the source fetched describes the building type
    but does not mention Shenzhen anywhere, so recording it here would
    assert a municipal tie the source does not support.
  - 盆菜 (poon choi / basin dish): the URL tried returned HTTP 404, and
    nothing is recorded from a page that was not read.

  So there is no :dish, no :festival and no :craft entry. `by-kind`
  correctly returns empty for all three rather than a plausible filler. The
  honest read of this catalog is 'a 45-year-old city with two Ming-era
  walled towns inside its boundary', not 'Shenzhen has little culture'."
  (:require [kotoba.lang.text :as str]))

(def catalog
  "municipality-slug -> vector of culture entries."
  {"shenzhen"
   [{:culture/id "shenzhen.heritage.dapeng-fortress"
     :culture/name "Dapeng Fortress"
     :culture/name-local "大鹏城"
     :culture/municipality "shenzhen"
     :culture/country "CHN"
     :culture/kind :heritage
     :culture/summary "A walled village in Longgang District, Shenzhen, built in 1394 to protect the area from pirates; listed as a cultural relics protection unit in Shenzhen in 1983 and recognised as a provincial cultural relics protection unit by Guangdong in 1989."
     :culture/url "https://en.wikipedia.org/wiki/Dapeng_Fortress"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-27"}
    {:culture/id "shenzhen.heritage.nantou"
     :culture/name "Nantou (Xin'an Ancient City)"
     :culture/name-local "南头"
     :culture/municipality "shenzhen"
     :culture/country "CHN"
     :culture/kind :heritage
     :culture/summary "A historical walled town in Nanshan District, Shenzhen, and the former administrative centre of Xin'an County; established around 331 CE as the capital of Dongguan Prefecture, fortified in 736 CE under the Tang, with the present walled city built in 1394 under the Ming for coastal defence. The source states no formal heritage designation, only that it has been renamed 新安古城 and given a municipal museum."
     :culture/url "https://en.wikipedia.org/wiki/Nantou,_Shenzhen"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-27"}
    {:culture/id "shenzhen.civic-history.first-special-economic-zone"
     :culture/name "China's first Special Economic Zone"
     :culture/name-local "经济特区"
     :culture/municipality "shenzhen"
     :culture/country "CHN"
     :culture/kind :civic-history
     :culture/summary "In May 1980 the Central Committee designated Shenzhen as the first Special Economic Zone in China, promoted by Deng Xiaoping as part of the reform and opening up policy. This is also the source of the SEZ legislative power under which one of this repo's two catalogued regulations was enacted."
     :culture/url "https://en.wikipedia.org/wiki/Shenzhen"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-27"}]})

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
      :note (str "cloud-itonami-municipality-chn-shenzhen culture catalog "
                 "(ADR-2607171400): " (count (get catalog "shenzhen"))
                 " Shenzhen entries, each fetched and read. DELIBERATELY "
                 "SMALLER than the Beijing/Kyoto siblings: no :dish, no "
                 ":festival, no :craft. Hakka walled villages were dropped "
                 "because the source does not mention Shenzhen, and 盆菜 "
                 "because the URL tried returned HTTP 404. Extend "
                 "`culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [muni kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis muni)))

(defn ming-era-walled-towns
  "The two 1394 walled towns inside the modern city boundary -- the reason
  a 45-year-old municipality has any heritage catalog at all."
  [muni]
  (filterv #(str/includes? (:culture/summary %) "1394") (spec-basis muni)))
