# cloud-itonami-municipality-chn-shenzhen

Municipal-ordinance compliance catalog for **Shenzhen** (深圳市) — the third
CHN member of the `cloud-itonami-municipality-*` compliance-fact family
(ADR-2607141700), after
[`-chn-beijing`](https://github.com/cloud-itonami/cloud-itonami-municipality-chn-beijing)
and
[`-chn-shanghai`](https://github.com/cloud-itonami/cloud-itonami-municipality-chn-shanghai).

## Why Shenzhen is the interesting one

Shenzhen legislates under **two different instruments**, and the catalog
deliberately holds one of each — because the difference is a real
compliance fact, not a naming quirk:

| | Instrument | Provincial approval? |
|---|---|---|
| 深圳经济特区控制吸烟条例 | 经济特区法规 (SEZ regulation) | **No** — SEZ power, held since Shenzhen became China's first SEZ in May 1980 |
| 深圳市生活垃圾分类管理条例 | 地方性法规 of a 较大的市 | **Yes** — its own announcement records 广东省第十三届人大常委会 approving it on 2020-06-23, between municipal passage (2019-12-31) and effect (2020-09-01) |

Beijing and Shanghai are 直辖市 and have no equivalent approval step; their
sibling catalogs show none. `ordinance.facts/sez-regulations` and
`provincially-approved` expose the split, and tests assert the SEZ entry is
*not* the provincially-approved one.

## Provenance: two different hosts, and that is recorded

- The waste ordinance was read from **cgj.sz.gov.cn** (深圳市城市管理和综合
  执法局, a municipal body) → `:official-sz-gov-cn`.
- The smoking ordinance's full verbatim header was found only on
  **www.szns.gov.cn** — 深圳市南山区人民政府, a **district** government on
  its **own domain** → `:official-shenzhen-district-gov-cn`.

`szns.gov.cn` is **not** a subdomain of `sz.gov.cn`. An earlier draft of
this repo casually asserted both citations were "on sz.gov.cn"; that was
false, a test now pins the distinction, and the ns docstring records the
mistake. The municipal 卫健委 (`wjw.sz.gov.cn`) publishes a 政策解读 of the
same 2019 revision — it corroborates the date but is an interpretation, not
the text, so it is not cited as the source.

## The culture catalog is small on purpose

3 entries, and **no `:dish`, `:festival` or `:craft`**. Shenzhen became an
SEZ in 1980; the Beijing and Kyoto siblings each carry 8–9 entries from
centuries of accumulated tradition, and Shenzhen has no equivalent stock of
*verifiable, municipality-scoped* items. Two candidates were investigated
and dropped rather than padded in:

- **Hakka walled villages** — the source describes the building type but
  does not mention Shenzhen anywhere.
- **盆菜 (poon choi)** — the URL tried returned HTTP 404.

What is recorded: the two Ming-era walled towns inside the modern boundary
(Dapeng Fortress and Nantou, both 1394) and the 1980 SEZ designation. Note
that **Nantou records no heritage designation** because its source stated
none, while **Dapeng records both** (Shenzhen 1983, Guangdong 1989) because
its source did. Tests assert all of this.

The honest read is "a 45-year-old city with two Ming walled towns inside
it", not "Shenzhen has little culture".

## Data

`src/ordinance/facts.cljk`, `src/culture/facts.cljk`, schemas identical to
every sibling (a test asserts every attribute used is declared), and
generated `data/*-tx.edn` (tests assert they match the catalogs).

## License

AGPL-3.0-or-later.

## Running it

`kbb -M:test` (19 tests, 65 assertions) and `kbb -M:lint`.
