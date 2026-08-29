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
 * the height those rows have *together* — read straight off the two row
 * elements, whose sum is fixed by the viewport, the HUD and the hand
 * reservations however the pair is split — plus both players' row stats, and
 * asks `solvePooledLayout` for one card width and the slot height each side
 * needs. GameBoard turns the heights into the grid's row weights and hands the
 * layout to both `Battlefield`s through `PooledBattlefieldLayoutContext`.
 *
 * Measured from the rows rather than derived (container − HUD − reservations)
 * on purpose: the reservations come from the window-derived responsive sizes,
 * which update synchronously on `resize`, while element measurements arrive a
 * frame later through ResizeObserver — mixing the two solved one frame with
 * the new reservation against the old container height, oversizing the cards.
 * Reading the rows keeps every input in one DOM state.
 *
 * No feedback loop: the rows' *sum* does not depend on the weights this hook
 * produces, and the slot width comes from the fixed command-zone and zone-pile
 * columns, never from the cards.
 *
 * Returns null while disabled (multiplayer, where strip cells size themselves)
 * or before the first measurement, in which case each battlefield falls back
 * to its own per-slot solve. `Battlefield` additionally clamps the pooled width
 * to what its own measured slot fits, so a stale frame can never overflow.
 */
export function usePooledBattlefieldLayout({
  enabled,
  opponentRowRef,
  playerRowRef,
  slotRef,
  player,
  opponent,
  base,
}: {
  enabled: boolean
  /** Grid row 2 — the opponent board area. */
  opponentRowRef: RefObject<HTMLElement | null>
  /** Grid row 4 — the player board area. */
  playerRowRef: RefObject<HTMLElement | null>
  /** One battlefield's slot — both slots share a width in the two-player grid. */
  slotRef: RefObject<HTMLElement | null>
  player: BoardStats
  opponent: BoardStats
  base: ResponsiveSizes
}): PooledLayout | null {
  const opponentRow = useObservedSize(opponentRowRef, enabled)
  const playerRow = useObservedSize(playerRowRef, enabled)
  const slot = useObservedSize(slotRef, enabled)

  const pooledHeight = (opponentRow?.height ?? 0) + (playerRow?.height ?? 0)
  const slotWidth = slot?.width ?? 0
  const { front: pf, back: pb } = player
  const { front: of, back: ob } = opponent
  return useMemo(() => {
    if (!enabled || slotWidth <= 0 || pooledHeight <= 0) return null
    return solvePooledLayout(slotWidth, pooledHeight, { front: pf, back: pb }, { front: of, back: ob }, layoutEnvFor(base))
    // Keyed on the stats' numbers so an unrelated store update that rebuilds
    // equal stats doesn't produce a fresh layout identity (which would re-render
    // both battlefields for no visual change).
  }, [
    enabled,
    slotWidth,
    pooledHeight,
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
