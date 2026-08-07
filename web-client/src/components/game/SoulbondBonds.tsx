import { useEffect, useState } from 'react'
import { useGameStore } from '@/store/gameStore.ts'
import { selectGameState } from '@/store/selectors.ts'
import type { EntityId } from '@/types'
import { ZoneType } from '@/types'

interface Point {
  x: number
  y: number
}

interface BondData {
  /** `${a}|${b}` with the ids sorted, so a pair yields one bond however it is walked. */
  key: string
  from: Point
  to: Point
}

/** Center of a battlefield card's DOM node, or null if it isn't rendered right now. */
function getCardCenter(cardId: EntityId): Point | null {
  const element = document.querySelector(`[data-card-id="${cardId}"]`)
  if (!element) return null
  const rect = element.getBoundingClientRect()
  return { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 }
}

/** Is this viewport point on-screen? Mirrors CombatArrows — a slid-away board must not paint. */
function isOnScreen(p: Point): boolean {
  return p.x >= -4 && p.x <= window.innerWidth + 4
}

const BOND_COLOR = '#a78bfa'
const BOND_GLOW = '#e9d5ff'

/**
 * One soulbond link (CR 702.95b): two spirit strands braided between the paired creatures, with
 * motes of light drifting along the bond and a soft pulse at each anchor.
 *
 * The two strands are the same quadratic curve bowed to opposite sides, which reads as a twist
 * without needing a real 3D helix, and stays legible at any distance between the two slots. The
 * strand offsets and the mote timings are derived from [phase] so several bonds on one board don't
 * animate in lockstep.
 */
function Bond({ from, to, phase }: { from: Point; to: Point; phase: number }) {
  const midX = (from.x + to.x) / 2
  const midY = (from.y + to.y) / 2
  const dx = to.x - from.x
  const dy = to.y - from.y
  const distance = Math.hypot(dx, dy) || 1

  // Bow perpendicular to the bond so the braid stays symmetric at any angle.
  const bow = Math.min(distance * 0.22, 52)
  const nx = -dy / distance
  const ny = dx / distance

  const strand = (side: 1 | -1) =>
    `M ${from.x} ${from.y} Q ${midX + nx * bow * side} ${midY + ny * bow * side} ${to.x} ${to.y}`

  // Three motes per strand, evenly spaced and offset by the bond's own phase.
  const motes = [0, 1, 2].flatMap((i) =>
    ([1, -1] as const).map((side) => {
      const t = ((phase + i / 3 + (side === 1 ? 0 : 1 / 6)) % 1)
      const mt = 1 - t
      const cx = midX + nx * bow * side
      const cy = midY + ny * bow * side
      return {
        key: `${i}-${side}`,
        x: mt * mt * from.x + 2 * mt * t * cx + t * t * to.x,
        y: mt * mt * from.y + 2 * mt * t * cy + t * t * to.y,
        // Fade in and out over the traverse so motes appear to condense and dissipate.
        opacity: Math.sin(t * Math.PI) * 0.9,
      }
    }),
  )

  // Anchor halos breathe together — the "these two are one" beat.
  const breathe = 0.55 + 0.45 * Math.sin(phase * Math.PI * 2)

  return (
    <g>
      {/* Outer glow: both strands, thick and faint */}
      <path d={strand(1)} fill="none" stroke={BOND_GLOW} strokeWidth={9} strokeOpacity={0.14} strokeLinecap="round" />
      <path d={strand(-1)} fill="none" stroke={BOND_GLOW} strokeWidth={9} strokeOpacity={0.14} strokeLinecap="round" />
      {/* The braid itself */}
      <path d={strand(1)} fill="none" stroke={BOND_COLOR} strokeWidth={2.5} strokeOpacity={0.85} strokeLinecap="round" />
      <path d={strand(-1)} fill="none" stroke={BOND_COLOR} strokeWidth={2.5} strokeOpacity={0.85} strokeLinecap="round" />
      {/* Motes drifting along the strands */}
      {motes.map((m) => (
        <circle key={m.key} cx={m.x} cy={m.y} r={2.6} fill={BOND_GLOW} fillOpacity={m.opacity} />
      ))}
      {/* Anchor halos on each paired creature */}
      {[from, to].map((p, i) => (
        <g key={`anchor-${i}`}>
          <circle cx={p.x} cy={p.y} r={13 + breathe * 5} fill={BOND_COLOR} fillOpacity={0.1 * breathe} />
          <circle
            cx={p.x}
            cy={p.y}
            r={9}
            fill="none"
            stroke={BOND_GLOW}
            strokeWidth={1.6}
            strokeOpacity={0.35 + breathe * 0.35}
          />
        </g>
      ))}
    </g>
  )
}

/**
 * Overlay drawing a bond between every pair of soulbond-paired creatures on the battlefield
 * (CR 702.95b), so "which two are paired" is readable at a glance instead of buried in oracle text.
 *
 * Pairing is server state (`pairedWithId`, symmetric on both halves), so this component only reads
 * and renders — no client-side notion of who is paired with whom. Positions are re-measured on a
 * timer like `CombatArrows`, because cards move for reasons React doesn't re-render this component
 * for (battlefield reflow, board slide, window resize).
 */
export function SoulbondBonds() {
  const gameState = useGameStore(selectGameState)
  const cards = gameState?.cards
  const [bonds, setBonds] = useState<BondData[]>([])
  const [phase, setPhase] = useState(0)

  useEffect(() => {
    const measure = () => {
      if (!cards) {
        setBonds([])
        return
      }
      const next: BondData[] = []
      const seen = new Set<string>()

      for (const [idStr, card] of Object.entries(cards)) {
        const id = idStr as EntityId
        const partnerId = card.pairedWithId
        if (!partnerId) continue
        if (card.zone?.zoneType !== ZoneType.BATTLEFIELD) continue
        if (cards[partnerId]?.zone?.zoneType !== ZoneType.BATTLEFIELD) continue

        // One bond per pair: the sorted id pair is the identity.
        const key = id < partnerId ? `${id}|${partnerId}` : `${partnerId}|${id}`
        if (seen.has(key)) continue

        const from = getCardCenter(id)
        const to = getCardCenter(partnerId)
        // Both halves must be visible — a bond with one anchor off a slid-away board would
        // stripe across the screen edge.
        if (!from || !to || !isOnScreen(from) || !isOnScreen(to)) continue

        seen.add(key)
        next.push({ key, from, to })
      }
      setBonds(next)
    }

    measure()
    const interval = setInterval(measure, 100)
    return () => clearInterval(interval)
  }, [cards])

  // Drive the drift/breathe animation. Only runs while something is actually paired.
  useEffect(() => {
    if (bonds.length === 0) return
    let raf = 0
    const start = performance.now()
    const tick = (now: number) => {
      // One full traverse every 2.6s.
      setPhase(((now - start) / 2600) % 1)
      raf = requestAnimationFrame(tick)
    }
    raf = requestAnimationFrame(tick)
    return () => cancelAnimationFrame(raf)
  }, [bonds.length])

  if (bonds.length === 0) return null

  return (
    <svg
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100vw',
        height: '100vh',
        pointerEvents: 'none',
        // Under the combat/targeting arrows (2000) — a pair bond is ambient board state and must
        // never obscure "what is attacking what".
        zIndex: 1900,
      }}
    >
      {bonds.map((bond, i) => (
        // Stagger each bond's phase so multiple pairs don't pulse as one.
        <Bond key={bond.key} from={bond.from} to={bond.to} phase={(phase + i * 0.37) % 1} />
      ))}
    </svg>
  )
}
