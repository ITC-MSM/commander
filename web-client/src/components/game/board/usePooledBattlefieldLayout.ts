import { useLayoutEffect, useMemo, useState, type RefObject } from 'react'
import type { ResponsiveSizes } from '@/hooks/useResponsive'
import { solvePooledLayout, type BoardStats, type PooledLayout } from './battlefieldLayout'
import { layoutEnvFor } from './shared'

interface Size {
  width: number
  height: number
}

/** Live border-box size of an element, or null until it has been measured (or while disabled). */
function useObservedSize(ref: RefObject<HTMLElement | null>, enabled: boolean): Size | null {
  const [size, setSize] = useState<Size | null>(null)
  useLayoutEffect(() => {
    if (!enabled) {
      setSize(null)
      return
    }
    const node = ref.current
    if (!node) return
    const rect = node.getBoundingClientRect()
    setSize({ width: rect.width, height: rect.height })
    const obs = new ResizeObserver((entries) => {
      const entry = entries[0]
      if (entry) setSize({ width: entry.contentRect.width, height: entry.contentRect.height })
    })
    obs.observe(node)
    return () => obs.disconnect()
  }, [ref, enabled])
  return size
}

/**
 * Two-player battlefield sizing, solved for both players at once.
 *
 * The board grid gives the two battlefields rows 2 and 4; this hook measures
 * the height those rows have *together* (the container minus the hand
 * reservation rows and the center HUD) and both players' row stats, and asks
 * `solvePooledLayout` for one card width plus the slot height each side needs.
 * GameBoard turns the heights into the grid's row weights and hands the layout
 * to both `Battlefield`s through `PooledBattlefieldLayoutContext`.
 *
 * No feedback loop: every measured input is independent of the layout it
 * produces — the container is viewport-sized, the center HUD's `auto` height
 * depends only on its own content, and the slot width comes from the fixed
 * command-zone and zone-pile columns, never from the cards.
 *
 * Returns null while disabled (multiplayer, where strip cells size themselves)
 * or before the first measurement, in which case each battlefield falls back
 * to its own per-slot solve.
 */
export function usePooledBattlefieldLayout({
  enabled,
  containerRef,
  centerRef,
  slotRef,
  reservedHeight,
  player,
  opponent,
  base,
}: {
  enabled: boolean
  /** The board grid container (rows 1–5). */
  containerRef: RefObject<HTMLElement | null>
  /** The center HUD row (grid row 3, `auto`). */
  centerRef: RefObject<HTMLElement | null>
  /** One battlefield's slot — both slots share a width in the two-player grid. */
  slotRef: RefObject<HTMLElement | null>
  /** Sum of the hand reservation rows (1 and 5). */
  reservedHeight: number
  player: BoardStats
  opponent: BoardStats
  base: ResponsiveSizes
}): PooledLayout | null {
  const container = useObservedSize(containerRef, enabled)
  const center = useObservedSize(centerRef, enabled)
  const slot = useObservedSize(slotRef, enabled)

  const containerHeight = container?.height ?? 0
  const centerHeight = center?.height ?? 0
  const slotWidth = slot?.width ?? 0
  const centerMeasured = center !== null
  const { front: pf, back: pb } = player
  const { front: of, back: ob } = opponent
  return useMemo(() => {
    if (!enabled || slotWidth <= 0 || containerHeight <= 0 || !centerMeasured) return null
    const pooledHeight = containerHeight - centerHeight - reservedHeight
    if (pooledHeight <= 0) return null
    return solvePooledLayout(slotWidth, pooledHeight, { front: pf, back: pb }, { front: of, back: ob }, layoutEnvFor(base))
    // Keyed on the stats' numbers so an unrelated store update that rebuilds
    // equal stats doesn't produce a fresh layout identity (which would re-render
    // both battlefields for no visual change).
  }, [
    enabled,
    slotWidth,
    containerHeight,
    centerMeasured,
    centerHeight,
    reservedHeight,
    base,
    pf.count,
    pf.tapped,
    pf.stackedExtra,
    pb.count,
    pb.tapped,
    pb.stackedExtra,
    of.count,
    of.tapped,
    of.stackedExtra,
    ob.count,
    ob.tapped,
    ob.stackedExtra,
  ])
}
