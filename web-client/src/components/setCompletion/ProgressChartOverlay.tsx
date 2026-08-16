import { useEffect, useLayoutEffect, useMemo, useState } from 'react'
import { fetchProgressHistory, type ProgressPoint } from '@/api/setCoverage'
import styles from './SetCompletionPage.module.css'

// The chart is drawn in real pixels: the viewBox mirrors the measured size of its container, so the
// plot always fills exactly the space the modal has left over and never overflows the viewport.
// Labels keep their true font size at every size because nothing is scaled.
//
// Two stacked panels share one x scale rather than one plot carrying two y scales: the cumulative
// curve (0–10k) and the daily adds (0–800) have no common unit, so overlaying them on a shared plot
// would invent a relationship between two arbitrary scales.
const MIN_W = 320
const MIN_H = 200
const BOT_FRAC = 0.26 // share of plot height given to the daily-adds panel
const PANEL_GAP = 34 // room between the panels for the lower panel's caption
const Y_STEPS = [50, 100, 250, 500, 1000, 2000, 2500, 5000, 10000, 20000]
const MONTH_GAP = 40 // px of breathing room required between two month labels
const MAX_BAR = 24 // mark spec: bars never fill their slot
const BAR_AIR = 2 // surface gap between neighbouring bars

// Both hues are design-system tokens, validated against this surface for lightness, chroma, CVD
// separation and contrast. Axis and label text stays on text tokens — only marks wear a series hue.
const C_TOTAL = '#2196f3' // --color-accent-dark
const C_ADDED = '#d97706' // --color-ability
const SURFACE = '#141824'

const fmt = (n: number) => n.toLocaleString('en-US')
const niceFull = (s: string) =>
  new Date(`${s}T00:00:00`).toLocaleDateString('en-US', { weekday: 'short', month: 'long', day: 'numeric' })
// January carries its year, so the axis stays readable once labels get thinned out on a narrow chart.
const monthLabel = (s: string) => {
  const d = new Date(`${s}T00:00:00`)
  const m = d.toLocaleDateString('en-US', { month: 'short' })
  return d.getMonth() === 0 ? `${m} ’${String(d.getFullYear()).slice(2)}` : m
}

/** A column with a rounded data-end and a square baseline. */
function barPath(x: number, y: number, w: number, h: number): string {
  const r = Math.min(4, w / 2, h)
  return `M ${x} ${y + h} L ${x} ${y + r} Q ${x} ${y} ${x + r} ${y} L ${x + w - r} ${y} Q ${x + w} ${y} ${x + w} ${y + r} L ${x + w} ${y + h} Z`
}

/**
 * Tracks the rendered size of an element so the SVG can be laid out in pixels. Uses a callback ref
 * because the plot mounts only once the series has loaded — a plain ref would still be null when the
 * effect first ran, and the observer would never attach.
 */
function useElementSize<T extends HTMLElement>() {
  const [el, setEl] = useState<T | null>(null)
  const [size, setSize] = useState({ w: 0, h: 0 })
  useLayoutEffect(() => {
    if (!el) return
    const ro = new ResizeObserver(([entry]) => {
      const box = entry?.contentRect
      if (box) setSize({ w: Math.round(box.width), h: Math.round(box.height) })
    })
    ro.observe(el)
    return () => ro.disconnect()
  }, [el])
  return [setEl, size] as const
}

/** Modal showing implemented cards over time: a cumulative curve above a daily-adds panel. */
export function ProgressChartOverlay({ onClose }: { onClose: () => void }) {
  const [points, setPoints] = useState<readonly ProgressPoint[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [hoverI, setHoverI] = useState<number | null>(null)
  const [plotRef, plotSize] = useElementSize<HTMLDivElement>()

  useEffect(() => {
    let cancelled = false
    fetchProgressHistory()
      .then((d) => !cancelled && setPoints(d))
      .catch((e) => !cancelled && setError(e instanceof Error ? e.message : String(e)))
    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])

  const summary = useMemo(() => {
    if (!points || points.length === 0) return null
    const added = points.map((p) => p.added)
    const busiest = added.reduce((a, b) => Math.max(a, b), 0)
    return {
      final: points[points.length - 1]?.total ?? 0,
      days: points.length,
      activeDays: added.filter((a) => a > 0).length,
      busiest,
      busiestDate: points[added.indexOf(busiest)]?.date ?? null,
    }
  }, [points])

  const model = useMemo(() => {
    if (!points || points.length < 2 || plotSize.w === 0) return null
    const VW = Math.max(MIN_W, plotSize.w)
    const VH = Math.max(MIN_H, plotSize.h)
    const tight = VW < 620
    // The right gutter holds the curve's end label; too narrow for it, and the label is dropped.
    const M = { l: tight ? 46 : 62, r: tight ? 12 : 78, t: 22, b: 24 }
    const PW = VW - M.l - M.r
    const plotH = VH - M.t - M.b
    const botH = Math.max(48, Math.round((plotH - PANEL_GAP) * BOT_FRAC))
    const topH = plotH - PANEL_GAP - botH
    const topY = M.t
    const botY = topY + topH + PANEL_GAP

    const n = points.length
    const totals = points.map((p) => p.total)
    const added = points.map((p) => p.added)
    const final = totals[n - 1] ?? 0
    const busiest = added.reduce((a, b) => Math.max(a, b), 0)

    const maxY = Math.max(500, Math.ceil(final / 1000) * 1000)
    const addMax = Math.max(50, Math.ceil(busiest / 50) * 50)
    const X = (i: number) => M.l + (PW * i) / (n - 1)
    const Y = (v: number) => topY + topH - (topH * v) / maxY
    const Yb = (v: number) => botY + botH - (botH * v) / addMax

    // Gridline density follows the height we actually got, so a short panel doesn't stack its labels.
    const maxLines = Math.max(2, Math.floor(topH / 40))
    const yStep = Y_STEPS.find((s) => maxY / s <= maxLines) ?? maxY
    const yGrid: number[] = []
    for (let v = 0; v <= maxY; v += yStep) yGrid.push(v)

    const monthTicks: { i: number; label: string }[] = []
    let lastMonth = ''
    let lastX = -Infinity
    points.forEach((p, i) => {
      const m = p.date.slice(0, 7)
      if (m === lastMonth) return
      lastMonth = m
      if (X(i) - lastX < MONTH_GAP) return // too crowded to label without overlapping
      lastX = X(i)
      monthTicks.push({ i, label: monthLabel(p.date) })
    })

    let dArea = `M ${X(0)} ${Y(0)}`
    let dLine = ''
    points.forEach((_, i) => {
      const x = X(i)
      const y = Y(totals[i] ?? 0)
      dArea += ` L ${x} ${y}`
      dLine += `${i ? ' L' : 'M'} ${x} ${y}`
    })
    dArea += ` L ${X(n - 1)} ${Y(0)} Z`

    const bw = Math.max(1, Math.min(MAX_BAR, PW / (n - 1) - BAR_AIR))
    const bars = points.flatMap((p, i) =>
      p.added > 0 ? [{ i, d: barPath(X(i) - bw / 2, Yb(p.added), bw, botY + botH - Yb(p.added)) }] : [],
    )

    return {
      VW, VH, M, PW, topY, topH, botY, botH,
      n, totals, added, final, maxY, addMax, X, Y, Yb, yGrid, monthTicks, dArea, dLine, bars,
      showEndLabel: !tight,
    }
  }, [points, plotSize])

  function onMove(e: React.MouseEvent<SVGSVGElement>) {
    if (!model) return
    const r = e.currentTarget.getBoundingClientRect()
    const px = ((e.clientX - r.left) / r.width) * model.VW
    const i = Math.max(0, Math.min(model.n - 1, Math.round(((px - model.M.l) / model.PW) * (model.n - 1))))
    setHoverI(i)
  }

  const hoverPoint = hoverI != null ? points?.[hoverI] : undefined

  // Keep the tooltip inside the plot: flip it below the point near the top edge, and anchor it to
  // whichever side has room near the left/right edges.
  const tipTransform = (() => {
    if (!model || hoverI == null) return undefined
    const xPct = (model.X(hoverI) / model.VW) * 100
    const yPct = (model.Y(model.totals[hoverI] ?? 0) / model.VH) * 100
    const tx = xPct < 14 ? '0%' : xPct > 86 ? '-100%' : '-50%'
    return `translate(${tx}, ${yPct < 34 ? '18%' : '-118%'})`
  })()

  return (
    <div className={styles.overlayBackdrop} onClick={onClose}>
      <div className={styles.chartOverlay} onClick={(e) => e.stopPropagation()}>
        <header className={styles.chartHeader}>
          <div className={styles.chartHeadText}>
            <div className={styles.chartTitle}>Card implementation progress</div>
            <div className={styles.chartSub}>Distinct implemented cards, day by day since the project began</div>
          </div>
          <button className={styles.overlayClose} onClick={onClose} aria-label="Close">
            ✕
          </button>
        </header>

        {error && <div className={styles.error}>Couldn’t load progress: {error}</div>}
        {!points && !error && <div className={styles.loading}>Loading progress…</div>}

        {summary && (
          <>
            <div className={styles.chartStats}>
              <div className={styles.chartStat}>
                <div style={statK}>Distinct cards</div>
                <div style={statV}>
                  {fmt(summary.final)} <small style={statSmall}>implemented</small>
                </div>
              </div>
              <div className={styles.chartStat}>
                <div style={statK}>Active days</div>
                <div style={statV}>
                  {fmt(summary.activeDays)} <small style={statSmall}>of {summary.days}</small>
                </div>
              </div>
              <div className={styles.chartStat}>
                <div style={statK}>Busiest day</div>
                <div style={statV}>
                  +{fmt(summary.busiest)}{' '}
                  <small style={statSmall}>
                    {summary.busiestDate &&
                      new Date(`${summary.busiestDate}T00:00:00`).toLocaleDateString('en-US', {
                        month: 'short',
                        day: 'numeric',
                      })}
                  </small>
                </div>
              </div>
            </div>

            <div className={styles.chartBox}>
              <div className={styles.chartPlot} ref={plotRef}>
                {model && (
                  <svg
                    className={styles.chartSvg}
                    viewBox={`0 0 ${model.VW} ${model.VH}`}
                    onMouseMove={onMove}
                    onMouseLeave={() => setHoverI(null)}
                  >
                    <defs>
                      <linearGradient id="prgArea" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor={C_TOTAL} stopOpacity="0.16" />
                        <stop offset="100%" stopColor={C_TOTAL} stopOpacity="0.01" />
                      </linearGradient>
                    </defs>

                    {/* ---- cumulative panel ---- */}
                    <text x={model.M.l} y={model.topY - 9} style={panelCaption}>
                      Cumulative cards
                    </text>
                    {model.yGrid.map((v) => (
                      <g key={`y${v}`}>
                        <line
                          className={styles.chartGridLine}
                          x1={model.M.l}
                          y1={model.Y(v)}
                          x2={model.VW - model.M.r}
                          y2={model.Y(v)}
                        />
                        <text x={model.M.l - 10} y={model.Y(v) + 4} textAnchor="end" style={axisText}>
                          {fmt(v)}
                        </text>
                      </g>
                    ))}
                    <path d={model.dArea} fill="url(#prgArea)" />
                    <path
                      d={model.dLine}
                      fill="none"
                      stroke={C_TOTAL}
                      strokeWidth="2"
                      strokeLinejoin="round"
                      strokeLinecap="round"
                    />
                    {/* the one direct label on the curve: where it ends up today */}
                    <circle
                      cx={model.X(model.n - 1)}
                      cy={model.Y(model.final)}
                      r="4"
                      fill={C_TOTAL}
                      stroke={SURFACE}
                      strokeWidth="2"
                    />
                    {model.showEndLabel && (
                      <text
                        x={model.X(model.n - 1) + 10}
                        y={model.Y(model.final) + 4}
                        textAnchor="start"
                        style={endLabel}
                      >
                        {fmt(model.final)}
                      </text>
                    )}

                    {/* ---- daily-adds panel ---- */}
                    <text x={model.M.l} y={model.botY - 9} style={panelCaption}>
                      Cards added per day
                    </text>
                    <line
                      className={styles.chartGridLine}
                      x1={model.M.l}
                      y1={model.Yb(model.addMax)}
                      x2={model.VW - model.M.r}
                      y2={model.Yb(model.addMax)}
                    />
                    <line
                      className={styles.chartGridLine}
                      x1={model.M.l}
                      y1={model.Yb(0)}
                      x2={model.VW - model.M.r}
                      y2={model.Yb(0)}
                    />
                    <text x={model.M.l - 10} y={model.Yb(model.addMax) + 4} textAnchor="end" style={axisText}>
                      {fmt(model.addMax)}
                    </text>
                    <text x={model.M.l - 10} y={model.Yb(0) + 4} textAnchor="end" style={axisText}>
                      0
                    </text>
                    {model.bars.map((b) => (
                      <path key={`b${b.i}`} d={b.d} fill={C_ADDED} opacity={hoverI === b.i ? 1 : 0.78} />
                    ))}

                    {/* ---- shared x axis ---- */}
                    {model.monthTicks.map((t) => (
                      <text key={`m${t.i}`} x={model.X(t.i)} y={model.VH - 6} textAnchor="middle" style={axisText}>
                        {t.label}
                      </text>
                    ))}

                    {/* ---- hover crosshair, spanning both panels ---- */}
                    {hoverI != null && (
                      <>
                        <line
                          className={styles.chartCross}
                          x1={model.X(hoverI)}
                          y1={model.topY}
                          x2={model.X(hoverI)}
                          y2={model.botY + model.botH}
                        />
                        <circle
                          className={styles.chartDot}
                          cx={model.X(hoverI)}
                          cy={model.Y(model.totals[hoverI] ?? 0)}
                          r="4"
                          fill={C_TOTAL}
                        />
                      </>
                    )}
                  </svg>
                )}

                {model && hoverI != null && hoverPoint && (
                  <div
                    className={styles.chartTip}
                    style={{
                      left: `${(model.X(hoverI) / model.VW) * 100}%`,
                      top: `${(model.Y(model.totals[hoverI] ?? 0) / model.VH) * 100}%`,
                      ...(tipTransform ? { transform: tipTransform } : {}),
                    }}
                  >
                    <div style={tipDate}>{niceFull(hoverPoint.date)}</div>
                    <div style={tipRow}>
                      <span style={{ ...tipKey, background: C_TOTAL }} />
                      <strong style={tipValue}>{fmt(model.totals[hoverI] ?? 0)}</strong>
                      <span style={tipName}>cards total</span>
                    </div>
                    <div style={tipRow}>
                      <span style={{ ...tipKey, background: C_ADDED }} />
                      <strong style={tipValue}>
                        {(model.added[hoverI] ?? 0) > 0 ? `+${fmt(model.added[hoverI] ?? 0)}` : '—'}
                      </strong>
                      <span style={tipName}>that day</span>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  )
}

// Inline styles for the nested text spans (CSS-module class selectors can't target child class names)
// and for the SVG text, which has no class hooks of its own.
const statK: React.CSSProperties = { fontSize: 'var(--font-xs)', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em' }
const statV: React.CSSProperties = { fontSize: 'var(--font-xl)', fontWeight: 700, marginTop: 2 }
const statSmall: React.CSSProperties = { fontSize: 'var(--font-sm)', color: 'var(--text-faint)', fontWeight: 500 }
const axisText: React.CSSProperties = { fill: 'var(--text-muted)', fontSize: 11, fontVariantNumeric: 'tabular-nums' }
const panelCaption: React.CSSProperties = { fill: 'var(--text-secondary)', fontSize: 11, letterSpacing: '0.06em', textTransform: 'uppercase' }
const endLabel: React.CSSProperties = { fill: 'var(--text-primary)', fontSize: 12, fontWeight: 600 }
const tipDate: React.CSSProperties = { color: 'var(--text-muted)', fontSize: 'var(--font-xs)', marginBottom: 6 }
const tipRow: React.CSSProperties = { display: 'flex', alignItems: 'baseline', gap: 8, lineHeight: 1.5 }
const tipKey: React.CSSProperties = { width: 10, height: 2, borderRadius: 2, alignSelf: 'center' }
const tipValue: React.CSSProperties = { fontSize: 'var(--font-md)', fontWeight: 700, fontVariantNumeric: 'tabular-nums' }
const tipName: React.CSSProperties = { color: 'var(--text-muted)', fontSize: 'var(--font-sm)' }
