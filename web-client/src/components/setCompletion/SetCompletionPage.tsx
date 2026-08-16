import { memo, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { SetIcon } from '../ui/SetIcon'
import { HelpTip } from '../help/HelpTip'
import { HoverCardPreview } from '../ui/HoverCardPreview'
import { ProgressChartOverlay } from './ProgressChartOverlay'
import { getScryfallFallbackUrl } from '@/utils/cardImages'
import {
  fetchSetCoverage,
  fetchCoverageSummary,
  fetchSetDetail,
  type SetCoverage,
  type CoverageSummary,
  type SetDetail,
  type CardCoverage,
} from '@/api/setCoverage'
import styles from './SetCompletionPage.module.css'

type SortKey = 'newest' | 'percent' | 'implemented' | 'assayReady' | 'name'
type Filter = 'all' | 'standard' | 'inProgress' | 'complete'
type Tab = 'sets' | 'explorer'

const SORT_LABELS: Record<SortKey, string> = {
  newest: 'Newest',
  percent: 'Most complete',
  implemented: 'Most cards done',
  assayReady: 'Most Assay-ready',
  name: 'A–Z',
}

/**
 * Where game-server mounts the live Argentum Assay explorer.
 *
 * Mounted on *every* server, not just dev ones: the tool is a read over public card text and holds
 * no state a request can mutate. See `AssayExploreController` for what that costs and why it is
 * still worth it — and {@link ExplorerPane} for why there is consequently no flag to fetch.
 */
const EXPLORER_URL = '/api/assay/explorer'

/**
 * One line of English for a card's Assay verdict, for the tile tooltip.
 *
 * Returns null when the baked ledger has no row for the card. That is *unknown*, not "no", and the
 * tooltip stays silent rather than implying Assay was asked and refused.
 */
function assayTooltip(card: CardCoverage): string | null {
  const a = card.assay
  if (!a) return null
  if (a.readsWhole) {
    return card.implemented
      ? 'Assay reads this card whole.'
      : 'Assay reads this card whole — implementable with existing vocabulary.'
  }
  const where = a.line ? ` — “${a.line}”` : ''
  return `Assay declines this card: ${a.kind}${where}`
}

/** A card nobody has authored that Assay already reads end to end: the cheapest work on the board. */
function isAssayReady(card: CardCoverage): boolean {
  return !card.implemented && card.notPlanned === null && card.assay?.readsWhole === true
}

const FILTER_LABELS: Record<Filter, string> = {
  all: 'All',
  standard: 'Standard',
  inProgress: 'In progress',
  complete: 'Complete',
}

/** Coverage tier drives the bar/accent color. Mirrors the at-a-glance red→green of the TUI. */
function tier(pct: number): 'empty' | 'low' | 'mid' | 'high' | 'done' {
  if (pct >= 100) return 'done'
  if (pct >= 67) return 'high'
  if (pct >= 34) return 'mid'
  if (pct > 0) return 'low'
  return 'empty'
}

function year(releaseDate: string | null): string {
  return releaseDate?.slice(0, 4) ?? '—'
}

function fmtPct(pct: number): string {
  return Number.isInteger(pct) ? String(pct) : pct.toFixed(1)
}

/** Downscale a baked normal-size Scryfall CDN URL to the small variant for grid thumbnails. */
function smallArt(name: string, imageUri: string | null): string {
  if (imageUri) return imageUri.replace('/normal/', '/small/')
  return getScryfallFallbackUrl(name, 'small')
}

interface HoverState {
  name: string
  imageUri: string | null
  pos: { x: number; y: number }
}

export function SetCompletionPage() {
  const navigate = useNavigate()
  const [sets, setSets] = useState<readonly SetCoverage[] | null>(null)
  const [summary, setSummary] = useState<CoverageSummary | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [query, setQuery] = useState('')
  const [sort, setSort] = useState<SortKey>('newest')
  const [filter, setFilter] = useState<Filter>('all')
  const [openCode, setOpenCode] = useState<string | null>(null)
  const [showProgress, setShowProgress] = useState(false)
  const [tab, setTab] = useState<Tab>('sets')

  useEffect(() => {
    let cancelled = false
    fetchSetCoverage()
      .then((data) => !cancelled && setSets(data))
      .catch((e) => !cancelled && setError(e instanceof Error ? e.message : String(e)))
    fetchCoverageSummary()
      .then((data) => !cancelled && setSummary(data))
      .catch(() => {}) // banner falls back to summing the per-set rows
    return () => {
      cancelled = true
    }
  }, [])

  const totals = useMemo(() => {
    if (!sets) return null
    const implemented = sets.reduce((n, s) => n + s.implemented, 0)
    const total = sets.reduce((n, s) => n + s.total, 0)
    const complete = sets.filter((s) => s.percent >= 100).length
    return {
      implemented,
      total,
      complete,
      setCount: sets.length,
      percent: total === 0 ? 0 : Math.round((implemented * 1000) / total) / 10,
    }
  }, [sets])

  const visible = useMemo(() => {
    if (!sets) return []
    const q = query.trim().toLowerCase()
    const filtered = sets.filter((s) => {
      if (filter === 'standard' && !s.inStandard) return false
      if (filter === 'complete' && s.percent < 100) return false
      if (filter === 'inProgress' && (s.percent >= 100 || s.implemented === 0)) return false
      if (!q) return true
      return s.name.toLowerCase().includes(q) || s.code.toLowerCase().includes(q)
    })
    const byName = (a: SetCoverage, b: SetCoverage) => a.name.localeCompare(b.name)
    const sorted = [...filtered]
    switch (sort) {
      case 'percent':
        sorted.sort((a, b) => b.percent - a.percent || b.implemented - a.implemented || byName(a, b))
        break
      case 'implemented':
        sorted.sort((a, b) => b.implemented - a.implemented || byName(a, b))
        break
      case 'assayReady':
        // "Which set has the most free work in it" — the question the Assay join exists to answer.
        // A null (no ledger baked) sorts as 0 rather than dropping the set out of the list.
        sorted.sort((a, b) => (b.assayReady ?? 0) - (a.assayReady ?? 0) || byName(a, b))
        break
      case 'name':
        sorted.sort(byName)
        break
      case 'newest':
      default:
        sorted.sort((a, b) => (b.releaseDate ?? '').localeCompare(a.releaseDate ?? '') || a.code.localeCompare(b.code))
        break
    }
    return sorted
  }, [sets, query, sort, filter])

  // The explorer pane is a full-height frame rather than a block in the scrolling document, so the
  // page stops being its own scroll container while that tab is up and hands the height to the frame.
  const onExplorer = tab === 'explorer'
  return (
    <div className={onExplorer ? `${styles.page} ${styles.pageFramed}` : styles.page}>
      <header className={styles.topbar}>
        <button className={styles.backButton} onClick={() => navigate('/')}>
          ← Back to menu
        </button>
        <h1 className={styles.title}>Set Completion</h1>
        <div className={styles.segmented} role="tablist" aria-label="View">
          <button
            role="tab"
            aria-selected={tab === 'sets'}
            className={tab === 'sets' ? styles.segmentActive : styles.segment}
            onClick={() => setTab('sets')}
          >
            Sets
          </button>
          <button
            role="tab"
            aria-selected={tab === 'explorer'}
            className={tab === 'explorer' ? styles.segmentActive : styles.segment}
            onClick={() => setTab('explorer')}
          >
            Assay Explorer
          </button>
        </div>
        {/* Sibling of the tablist, never inside it: `HelpTip` is a button, and one nested in a
            `role="tab"` would be both invalid markup and a click that switches tabs. The explanation
            lives in `topics.ts` rather than in a `title=` so the popover and `/help` can't disagree. */}
        <HelpTip topicId="assay-explorer" label="What is the Assay Explorer?" />
        <div className={styles.topbarSpacer} />
      </header>

      {onExplorer && <ExplorerPane />}

      {!onExplorer && (
        <SetsTab
          sets={sets}
          summary={summary}
          totals={totals}
          visible={visible}
          error={error}
          query={query}
          setQuery={setQuery}
          sort={sort}
          setSort={setSort}
          filter={filter}
          setFilter={setFilter}
          onOpen={setOpenCode}
          onShowProgress={() => setShowProgress(true)}
        />
      )}

      {openCode && <SetDetailOverlay code={openCode} onClose={() => setOpenCode(null)} />}
      {showProgress && <ProgressChartOverlay onClose={() => setShowProgress(false)} />}
    </div>
  )
}

/**
 * The live Argentum Assay explorer, framed.
 *
 * Framed rather than reimplemented on purpose. `:oracle-assay` already serves this page against the
 * **live grammar** — every number in it comes out of the same `Touchstone` / `Differential` objects
 * the CLI gates print, so a rule you just edited is one server restart away from being re-measured.
 * A React port would be a second implementation of those views, free to drift from the gates it
 * claims to display, which is exactly what that module's "a view, never a second source of truth"
 * rule exists to stop. game-server mounts the same handlers under `/api/assay/explorer`, so this is
 * one `<iframe>` and no duplicated logic.
 *
 * Always available, so there is no flag to fetch and no tab that appears late. The server starts its
 * corpus sweep on the first request to the tool, reports progress through its own `status` route
 * while the page is already up, and degrades to "the sweep failed" with the live parser and rule
 * tree still working if it can't reach one — so there is no state this component has to anticipate.
 */
function ExplorerPane() {
  return (
    <div className={styles.explorerPane}>
      <iframe
        className={styles.explorerFrame}
        src={EXPLORER_URL}
        title="Argentum Assay Explorer"
        // Same origin, so the frame can talk to its own endpoints; it needs nothing else.
        sandbox="allow-scripts allow-same-origin allow-forms"
      />
    </div>
  )
}

/** The set grid and its banner — the page as it was before the explorer tab joined it. */
function SetsTab({
  sets,
  summary,
  totals,
  visible,
  error,
  query,
  setQuery,
  sort,
  setSort,
  filter,
  setFilter,
  onOpen,
  onShowProgress,
}: {
  sets: readonly SetCoverage[] | null
  summary: CoverageSummary | null
  totals: { implemented: number; total: number; complete: number; setCount: number; percent: number } | null
  visible: readonly SetCoverage[]
  error: string | null
  query: string
  setQuery: (q: string) => void
  sort: SortKey
  setSort: (s: SortKey) => void
  filter: Filter
  setFilter: (f: Filter) => void
  onOpen: (code: string) => void
  onShowProgress: () => void
}) {
  return (
    <>
      {totals && (
        <button
          className={`${styles.summary} ${styles.summaryButton}`}
          onClick={onShowProgress}
          title="View implementation progress over time"
        >
          {(() => {
            // Headline is the DISTINCT figure (reprints deduped by name) so it answers "how much of
            // Magic is covered" rather than "how many booster printings across all sets" — the latter
            // double-counts a staple once per set. Printings stay as a secondary line. Until the
            // summary endpoint loads we fall back to the printing-based sum of the per-set rows.
            const pct = summary ? summary.distinctPercent : totals.percent
            return (
              <>
                <div className={styles.summaryStat}>
                  <span className={styles.summaryValue}>{fmtPct(pct)}%</span>
                  <span className={styles.summaryLabel}>overall</span>
                </div>
                <div className={styles.summaryBarWrap}>
                  <div className={styles.summaryBarTrack}>
                    <div className={styles.summaryBarFill} style={{ width: `${Math.min(100, pct)}%` }} />
                  </div>
                  <div className={styles.summaryMeta}>
                    {summary ? (
                      <>
                        <span>
                          <strong>{summary.distinctImplemented.toLocaleString()}</strong> /{' '}
                          {summary.distinctTotal.toLocaleString()} distinct booster cards implemented
                        </span>
                        <span>
                          + <strong>{summary.extraDistinctImplemented.toLocaleString()}</strong> /{' '}
                          {summary.extraDistinctTotal.toLocaleString()} distinct extras (completionist)
                        </span>
                        <span>
                          <strong>{summary.printingsImplemented.toLocaleString()}</strong> /{' '}
                          {summary.printingsTotal.toLocaleString()} booster printings across all sets
                        </span>
                        <span>
                          <strong>{summary.setsComplete}</strong> / {summary.setCount} sets complete
                          {summary.distinctNotPlanned > 0 && (
                            <span
                              className={styles.summaryNote}
                              title="Cards needing mechanics the engine will never carry — ante, subgames, physical dexterity. They're excluded from every total here."
                            >
                              {' '}
                              ({summary.distinctNotPlanned} cards not planned)
                            </span>
                          )}
                        </span>
                      </>
                    ) : (
                      <>
                        <span>
                          <strong>{totals.implemented.toLocaleString()}</strong> / {totals.total.toLocaleString()}{' '}
                          booster printings implemented
                        </span>
                        <span>
                          <strong>{totals.complete}</strong> / {totals.setCount} sets complete
                        </span>
                      </>
                    )}
                  </div>
                </div>
                <span className={styles.summaryHint}>📈 View progress →</span>
              </>
            )
          })()}
        </button>
      )}

      <div className={styles.controls}>
        <input
          className={styles.search}
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Filter sets by name or code…"
          aria-label="Filter sets"
        />
        <div className={styles.segmented} role="group" aria-label="Filter by completion">
          {(Object.keys(FILTER_LABELS) as Filter[]).map((f) => (
            <button
              key={f}
              className={filter === f ? styles.segmentActive : styles.segment}
              onClick={() => setFilter(f)}
            >
              {FILTER_LABELS[f]}
            </button>
          ))}
        </div>
        <div className={styles.segmented} role="group" aria-label="Sort">
          {(Object.keys(SORT_LABELS) as SortKey[]).map((k) => (
            <button
              key={k}
              className={sort === k ? styles.segmentActive : styles.segment}
              onClick={() => setSort(k)}
            >
              {SORT_LABELS[k]}
            </button>
          ))}
        </div>
      </div>

      {error && <div className={styles.error}>Couldn’t load coverage: {error}</div>}
      {!sets && !error && <div className={styles.loading}>Loading set coverage…</div>}

      {sets && (
        <>
          {visible.length === 0 ? (
            <div className={styles.empty}>No sets match “{query}”.</div>
          ) : (
            <div className={styles.grid}>
              {visible.map((s) => (
                <SetCard key={s.code} set={s} onOpen={() => onOpen(s.code)} />
              ))}
            </div>
          )}
        </>
      )}
    </>
  )
}

function SetCard({ set, onOpen }: { set: SetCoverage; onOpen: () => void }) {
  const t = tier(set.percent)
  const remaining = set.total - set.implemented
  const notPlanned = set.notPlanned + set.extraNotPlanned
  return (
    <button className={styles.card} data-tier={t} onClick={onOpen} title={`View ${set.name} cards`}>
      <SetIcon code={set.code} className={styles.watermark} />
      <div className={styles.cardHead}>
        <SetIcon code={set.code} className={styles.cardIcon} title={set.name} />
        <div className={styles.cardTitle}>
          <span className={styles.cardName} title={set.name}>
            {set.name}
          </span>
          <span className={styles.cardMeta}>
            <span className={styles.codeBadge}>{set.code}</span>
            <span>{year(set.releaseDate)}</span>
            {set.block && <span className={styles.block}>{set.block}</span>}
            {set.inStandard && (
              <span className={styles.standardBadge} title="Currently legal in Standard">
                Standard
              </span>
            )}
          </span>
        </div>
        <span className={styles.percent}>{fmtPct(set.percent)}%</span>
      </div>

      <div className={styles.barTrack}>
        <div className={styles.barFill} style={{ width: `${Math.min(100, set.percent)}%` }} />
      </div>

      <div className={styles.cardFoot}>
        <span>
          <strong>{set.implemented}</strong> / {set.total} {set.extraTotal > 0 ? 'booster' : 'cards'}
        </span>
        {set.extraTotal > 0 && (
          <span className={styles.extra}>
            {set.extraImplemented}/{set.extraTotal} extra
          </span>
        )}
        {notPlanned > 0 && (
          <span
            className={styles.notPlannedTag}
            title={`${notPlanned} card${notPlanned === 1 ? '' : 's'} we won't implement (ante, subgames, dexterity) — not counted in the total`}
          >
            {notPlanned} not planned
          </span>
        )}
        {/* Only worth the pixels when there is free work: a "0 Assay-ready" tag on 900 sets would
            bury the sets where the number is the reason to open them. Null (no ledger) hides too. */}
        {set.assayReady != null && set.assayReady > 0 && (
          <span
            className={styles.assayTag}
            title={`Argentum Assay already reads ${set.assayReady} of this set's missing cards end to end — they need no new SDK vocabulary`}
          >
            ⚡ {set.assayReady} Assay-ready
          </span>
        )}
        {t === 'done' ? (
          <span className={styles.doneTag}>Complete</span>
        ) : (
          <span className={styles.remaining}>{remaining} to go</span>
        )}
      </div>
    </button>
  )
}

type CardFilter = 'all' | 'implemented' | 'missing' | 'assayReady' | 'notPlanned'

const CARD_FILTER_LABELS: Record<CardFilter, string> = {
  all: 'All',
  implemented: 'Implemented',
  missing: 'Missing',
  assayReady: '⚡ Assay-ready',
  notPlanned: 'Not planned',
}

function SetDetailOverlay({ code, onClose }: { code: string; onClose: () => void }) {
  const [detail, setDetail] = useState<SetDetail | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [cardFilter, setCardFilter] = useState<CardFilter>('all')
  const [hover, setHover] = useState<HoverState | null>(null)

  useEffect(() => {
    let cancelled = false
    setDetail(null)
    setError(null)
    fetchSetDetail(code)
      .then((d) => !cancelled && setDetail(d))
      .catch((e) => !cancelled && setError(e instanceof Error ? e.message : String(e)))
    return () => {
      cancelled = true
    }
  }, [code])

  // Close on Escape.
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])

  // Stable handler + memoized filtered lists so that a hover (high-frequency mousemove) only
  // re-renders the floating preview — not the whole ~280-tile grid. The position update is
  // coalesced to one per animation frame so a fast mousemove can't outrun the paint loop.
  const rafRef = useRef<number | null>(null)
  const pendingHover = useRef<HoverState | null>(null)
  const onHover = useCallback((h: HoverState | null) => {
    if (h === null) {
      if (rafRef.current != null) cancelAnimationFrame(rafRef.current)
      rafRef.current = null
      pendingHover.current = null
      setHover(null)
      return
    }
    pendingHover.current = h
    if (rafRef.current == null) {
      rafRef.current = requestAnimationFrame(() => {
        rafRef.current = null
        if (pendingHover.current) setHover(pendingHover.current)
      })
    }
  }, [])
  useEffect(() => () => {
    if (rafRef.current != null) cancelAnimationFrame(rafRef.current)
  }, [])
  // "Missing" means still to do, so the not-planned cards live in their own bucket rather than
  // padding a list of work that will never happen.
  const matches = useCallback(
    (c: CardCoverage) => {
      switch (cardFilter) {
        case 'implemented':
          return c.implemented
        case 'missing':
          return !c.implemented && c.notPlanned === null
        case 'assayReady':
          return isAssayReady(c)
        case 'notPlanned':
          return c.notPlanned !== null
        default:
          return true
      }
    },
    [cardFilter],
  )
  const draftCards = useMemo(() => detail?.draft.filter(matches) ?? [], [detail, matches])
  // Each extras section keeps its own tally, so filtering to "Missing" still shows a section as
  // e.g. 0/13 done rather than making the reader count tiles.
  const extraSections = useMemo(
    () => detail?.extraGroups.map((g) => ({ group: g, cards: g.cards.filter(matches) })) ?? [],
    [detail, matches],
  )
  const notPlannedCount = detail ? detail.notPlanned + detail.extraNotPlanned : 0

  // One list drives both the index and the body, so a section can never appear in the rail without
  // a heading to jump to (or vice versa).
  const sections = useMemo(
    () =>
      detail
        ? [
            {
              label: extraSections.length > 0 ? 'Draft Cards' : 'Cards',
              cards: draftCards,
              total: detail.draft.length,
              done: detail.implemented,
              of: detail.total,
            },
            ...extraSections.map(({ group, cards }) => ({
              label: group.label,
              cards,
              total: group.cards.length,
              done: group.implemented,
              of: group.total,
            })),
          ]
        : [],
    [detail, draftCards, extraSections],
  )

  // The index stays put while the card grid scrolls under it, so it needs the scroller and each
  // heading's node to translate a click into a scroll offset.
  const bodyRef = useRef<HTMLDivElement>(null)
  const sectionNodes = useRef(new Map<string, HTMLElement>())
  const [activeSection, setActiveSection] = useState<string | null>(null)
  const registerSection = useCallback((label: string, el: HTMLElement | null) => {
    if (el) sectionNodes.current.set(label, el)
    else sectionNodes.current.delete(label)
  }, [])

  const jumpTo = useCallback((label: string) => {
    const body = bodyRef.current
    const el = sectionNodes.current.get(label)
    if (!body || !el) return
    const top = body.scrollTop + el.getBoundingClientRect().top - body.getBoundingClientRect().top
    body.scrollTo({ top, behavior: 'smooth' })
  }, [])

  // Highlight whichever section the reader is actually in: the last one whose heading has reached
  // the top of the scroller. Coalesced to one measurement per frame — scroll fires far faster.
  useEffect(() => {
    const body = bodyRef.current
    if (!body || sections.length < 2) return
    let raf: number | null = null
    const measure = () => {
      raf = null
      const bodyTop = body.getBoundingClientRect().top
      let current: string | null = null
      for (const s of sections) {
        const el = sectionNodes.current.get(s.label)
        if (el && el.getBoundingClientRect().top - bodyTop <= 8) current = s.label
      }
      setActiveSection(current ?? sections[0]?.label ?? null)
    }
    const onScroll = () => {
      if (raf == null) raf = requestAnimationFrame(measure)
    }
    measure()
    body.addEventListener('scroll', onScroll, { passive: true })
    return () => {
      body.removeEventListener('scroll', onScroll)
      if (raf != null) cancelAnimationFrame(raf)
    }
  }, [sections])

  const t = detail ? tier(detail.percent) : 'empty'

  return (
    <div className={styles.overlayBackdrop} onClick={onClose}>
      <div className={styles.overlay} data-tier={t} onClick={(e) => e.stopPropagation()}>
        <header className={styles.overlayHeader}>
          <SetIcon code={code} className={styles.overlayIcon} />
          <div className={styles.overlayTitleBlock}>
            <span className={styles.overlayTitle}>{detail?.name ?? code}</span>
            <span className={styles.cardMeta}>
              <span className={styles.codeBadge}>{code}</span>
              {detail && <span>{year(detail.releaseDate)}</span>}
              {detail?.block && <span className={styles.block}>{detail.block}</span>}
            </span>
          </div>
          {detail && (
            <div className={styles.overlayStats}>
              <span className={styles.percent}>{fmtPct(detail.percent)}%</span>
              <span className={styles.overlayCounts}>
                <strong>{detail.implemented}</strong>/{detail.total} {detail.extraTotal > 0 ? 'booster' : 'cards'}
                {detail.extraTotal > 0 && (
                  <>
                    {' · '}
                    {detail.extraImplemented}/{detail.extraTotal} extra
                  </>
                )}
                {notPlannedCount > 0 && <> · {notPlannedCount} not planned</>}
                {detail.assayReady != null && detail.assayReady > 0 && (
                  <span className={styles.assayNote}>
                    {' · '}⚡ {detail.assayReady} Assay-ready
                    <span className={styles.assayNoteTip}>
                      <HelpTip topicId="assay-ready" size="sm" />
                    </span>
                  </span>
                )}
              </span>
            </div>
          )}
          <button className={styles.overlayClose} onClick={onClose} aria-label="Close">
            ✕
          </button>
        </header>

        {detail && (
          <div className={styles.barTrack} style={{ flexShrink: 0 }}>
            <div className={styles.barFill} style={{ width: `${Math.min(100, detail.percent)}%` }} />
          </div>
        )}

        <div className={styles.overlayControls}>
          {(Object.keys(CARD_FILTER_LABELS) as CardFilter[])
            // The not-planned tab only exists for the handful of sets that have such a card.
            .filter((f) => f !== 'notPlanned' || notPlannedCount > 0)
            // The Assay tab needs a baked ledger to mean anything. Hidden when `assayReady` is null
            // (nobody ran `just assay-bake`); shown at zero, because "no free work left in this set"
            // is a real answer and worth being able to read.
            .filter((f) => f !== 'assayReady' || detail?.assayReady != null)
            .map((f) => (
              <button
                key={f}
                className={cardFilter === f ? styles.segmentActive : styles.segment}
                onClick={() => setCardFilter(f)}
                title={
                  f === 'assayReady'
                    ? 'Missing cards Argentum Assay already reads end to end — implementable with the SDK vocabulary that exists today'
                    : undefined
                }
              >
                {CARD_FILTER_LABELS[f]}
                {f === 'notPlanned' && ` (${notPlannedCount})`}
                {f === 'assayReady' && ` (${detail?.assayReady ?? 0})`}
              </button>
            ))}
        </div>

        <div className={styles.overlayLayout}>
          {/* Pinned beside the grid rather than hidden in a menu (Scryfall's own set pages use a
              dropdown): what a set contains stays readable without scrolling a 280-tile grid. */}
          {sections.length > 1 && (
            <nav className={styles.sectionIndex} aria-label={`${code} sections`}>
              {sections.map((s) => (
                <button
                  key={s.label}
                  className={activeSection === s.label ? styles.indexItemActive : styles.indexItem}
                  onClick={() => jumpTo(s.label)}
                >
                  <span className={styles.indexLabel}>{s.label}</span>
                  <span className={styles.indexCount}>
                    {s.done}/{s.of}
                  </span>
                </button>
              ))}
            </nav>
          )}

          <div className={styles.overlayBody} ref={bodyRef}>
            {error && <div className={styles.error}>Couldn’t load cards: {error}</div>}
            {!detail && !error && <div className={styles.loading}>Loading {code} cards…</div>}
            {/* Scryfall's own set-page sectioning: the draftable pool first, then each
                completionist product that added cards of its own. */}
            {sections.map((s) => (
              <CardSection
                key={s.label}
                title={s.label}
                cards={s.cards}
                total={s.total}
                done={s.done}
                of={s.of}
                onHover={onHover}
                register={registerSection}
              />
            ))}
          </div>
        </div>
      </div>
      {hover && <HoverCardPreview name={hover.name} imageUri={hover.imageUri} pos={hover.pos} />}
    </div>
  )
}

const CardSection = memo(function CardSection({
  title,
  cards,
  total,
  done,
  of,
  onHover,
  register,
}: {
  title: string
  /** The cards passing the active filter — what actually renders. */
  cards: readonly CardCoverage[]
  /** Cards in the section before filtering; a section with none never renders at all. */
  total: number
  /** Implemented count for the whole section, so the tally survives filtering. */
  done: number
  /** Section cards we intend to build (not-planned ones excluded). */
  of: number
  onHover: (h: HoverState | null) => void
  /** Publishes this section's node so the index can scroll to it and track it. */
  register: (label: string, el: HTMLElement | null) => void
}) {
  if (total === 0) return null
  return (
    <section className={styles.cardSection} ref={(el) => register(title, el)}>
      <h3 className={styles.sectionTitle}>
        {title}{' '}
        <span className={styles.sectionCount}>
          {done}/{of}
        </span>
      </h3>
      {cards.length === 0 ? (
        <div className={styles.sectionEmpty}>Nothing here for this filter.</div>
      ) : (
        <div className={styles.cardImageGrid}>
          {cards.map((c) => (
            <CardTile key={c.name} card={c} onHover={onHover} />
          ))}
        </div>
      )}
    </section>
  )
})

const CardTile = memo(function CardTile({
  card,
  onHover,
}: {
  card: CardCoverage
  onHover: (h: HoverState | null) => void
}) {
  // A not-planned card is neither done nor outstanding, so it gets its own look and always
  // explains itself — the reason travels with the flag from the manifest to this tooltip.
  const className = card.implemented
    ? styles.tile
    : card.notPlanned
      ? styles.tileNotPlanned
      : styles.tileMissing
  const ready = isAssayReady(card)
  // Assay's reading is on every tooltip but only badges the actionable case. A chip on all ~280
  // tiles would be wallpaper; the one state worth spotting across a grid is "nobody built this and
  // the grammar already reads it", which is a card you could pick up right now.
  const tooltip = [
    card.notPlanned ? `${card.name} — not planned: ${card.notPlanned.why}` : card.name,
    assayTooltip(card),
  ]
    .filter(Boolean)
    .join('\n')
  return (
    <div
      className={className}
      data-assay-ready={ready || undefined}
      title={tooltip}
      onMouseEnter={(e) => onHover({ name: card.name, imageUri: card.imageUri, pos: { x: e.clientX, y: e.clientY } })}
      onMouseMove={(e) => onHover({ name: card.name, imageUri: card.imageUri, pos: { x: e.clientX, y: e.clientY } })}
      onMouseLeave={() => onHover(null)}
    >
      <img
        className={styles.tileImage}
        src={smallArt(card.name, card.imageUri)}
        alt={card.name}
        loading="lazy"
        decoding="async"
      />
      <span className={styles.tileBadge}>
        {card.implemented ? '✓' : card.notPlanned ? card.notPlanned.kind : 'Missing'}
      </span>
      {ready && (
        <span className={styles.assayBadge} aria-label="Assay reads this card whole">
          ⚡
        </span>
      )}
      <span className={styles.tileName}>{card.name}</span>
    </div>
  )
})
