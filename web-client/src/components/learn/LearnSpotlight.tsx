/**
 * A ring around one part of the game board — the thing the coach is talking about.
 *
 * The board is not told about the coach. The ring is a fixed, pointer-transparent box portalled
 * to `<body>`, positioned from the target's `getBoundingClientRect()` and re-measured on a short
 * interval, so it follows the hand fanning out, the pass button changing width with its label,
 * and the combat buttons appearing. A spot with no on-screen element draws nothing.
 */
import { useEffect, useState } from 'react'
import { createPortal } from 'react-dom'
import { findSpot, spotRect, type SpotContext, type SpotId } from '@/learn/spots'
import styles from './LearnCoach.module.css'

interface Box {
  top: number
  left: number
  width: number
  height: number
}

const PAD = 6

function measure(spot: SpotId, ctx: SpotContext): Box | null {
  const el = findSpot(spot, ctx)
  if (!el) return null
  const r = spotRect(el)
  return { top: r.top - PAD, left: r.left - PAD, width: r.width + PAD * 2, height: r.height + PAD * 2 }
}

function same(a: Box | null, b: Box | null): boolean {
  if (a === null || b === null) return a === b
  return a.top === b.top && a.left === b.left && a.width === b.width && a.height === b.height
}

export function LearnSpotlight({
  spot,
  ctx,
  strong,
}: {
  spot: SpotId | undefined
  ctx: SpotContext
  /** The tour's ring is loud; a tip's ring is a quiet reminder. */
  strong: boolean
}) {
  const [box, setBox] = useState<Box | null>(null)

  useEffect(() => {
    if (!spot) {
      setBox(null)
      return
    }
    let last: Box | null = null
    const tick = () => {
      const next = measure(spot, ctx)
      if (!same(last, next)) {
        last = next
        setBox(next)
      }
    }
    tick()
    const id = window.setInterval(tick, 200)
    window.addEventListener('resize', tick)
    return () => {
      window.clearInterval(id)
      window.removeEventListener('resize', tick)
    }
  }, [spot, ctx])

  if (!spot || !box) return null
  return createPortal(
    <div
      key={spot}
      className={`${styles.spotlight} ${strong ? styles.spotlightStrong : ''}`}
      style={{ top: box.top, left: box.left, width: box.width, height: box.height }}
      aria-hidden="true"
    />,
    document.body,
  )
}
