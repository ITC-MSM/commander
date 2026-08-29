import { useMemo } from 'react'
import {
  groupCards,
  useBattlefieldCards,
  useSplitOutTargetIds,
  visibleStackDepth,
  type GroupedCard,
} from '@/store/selectors.ts'
import type { EntityId } from '@/types'
import type { BoardStats, RowStats } from './battlefieldLayout'

/** One battlefield's permanents grouped into rendered stacks, plus the footprint stats the sizing solver needs. */
export interface BoardGroups {
  lands: readonly GroupedCard[]
  creatures: readonly GroupedCard[]
  planeswalkers: readonly GroupedCard[]
  other: readonly GroupedCard[]
  stats: BoardStats
}

/**
 * Per-row footprint stats for the fit constraints in `battlefieldLayout.ts`.
 *
 * Tapped stacks are rotated 90° on the battlefield — their horizontal footprint
 * is cardHeight (≈1.4 × cardWidth) rather than cardWidth — and every card
 * stacked behind a group's first adds a fixed peek offset. Counted per row so
 * the horizontal-fit constraint reserves the true width; otherwise a crowded
 * row on a narrow viewport overflows into an unbudgeted wrap line, which pushes
 * the row up into the center HUD.
 *
 * Counts are *rendered stacks* (after `groupCards`), not raw cards: a collapsed
 * horde paints at most MAX_VISUAL_STACK_DEPTH cards, so the footprint uses the
 * capped depth (`visibleStackDepth`), not the raw count.
 */
export function rowStats(...groupLists: (readonly GroupedCard[])[]): RowStats {
  let count = 0
  let tapped = 0
  let stackedExtra = 0
  for (const groups of groupLists) {
    for (const group of groups) {
      count++
      // Every member of a group shares its tapped state (it's part of the
      // group key), so the representative answers for the whole stack.
      if (group.card.isTapped) tapped++
      stackedExtra += visibleStackDepth(group.count) - 1
    }
  }
  return { count, tapped, stackedExtra }
}

/**
 * Groups one side's permanents into stacks (see `groupCards`) and derives the
 * row stats. Used by `Battlefield` to render, and by `GameBoard`'s pooled
 * two-player solve, which needs both sides' stats before either battlefield
 * renders — grouping twice per update is far cheaper than plumbing the groups
 * up through the board.
 *
 * `isOpponent` + `playerId` follow `useBattlefieldCards`: an opponent board
 * scoped to one seat (multiplayer strip cells) or, omitted, every non-viewing
 * seat; a player-side board optionally showing another seat's permanents.
 *
 * Memoized so the arrays keep stable identity across unrelated store updates —
 * otherwise every re-render allocates fresh arrays that cascade into child
 * re-renders and invalidate downstream useMemos. Permanents that are chosen
 * targets / triggering sources keep their own card so their targeting arrows
 * can anchor (a member hidden behind the stack render cap would drop its
 * arrow) — see `useSplitOutTargetIds` / `groupCards`.
 */
export function useBoardGroups(isOpponent: boolean, playerId?: EntityId): BoardGroups {
  const cards = useBattlefieldCards(isOpponent ? playerId : undefined, isOpponent ? undefined : playerId)
  const lands = isOpponent ? cards.opponentLands : cards.playerLands
  const creatures = isOpponent ? cards.opponentCreatures : cards.playerCreatures
  const planeswalkers = isOpponent ? cards.opponentPlaneswalkers : cards.playerPlaneswalkers
  const other = isOpponent ? cards.opponentOther : cards.playerOther

  const splitOutIds = useSplitOutTargetIds()
  const groupedLands = useMemo(() => groupCards(lands, splitOutIds), [lands, splitOutIds])
  const groupedCreatures = useMemo(() => groupCards(creatures, splitOutIds), [creatures, splitOutIds])
  const groupedPlaneswalkers = useMemo(() => groupCards(planeswalkers, splitOutIds), [planeswalkers, splitOutIds])
  const groupedOther = useMemo(() => groupCards(other, splitOutIds), [other, splitOutIds])

  const stats = useMemo<BoardStats>(
    () => ({
      front: rowStats(groupedCreatures, groupedPlaneswalkers),
      back: rowStats(groupedLands, groupedOther),
    }),
    [groupedCreatures, groupedPlaneswalkers, groupedLands, groupedOther],
  )

  return useMemo(
    () => ({
      lands: groupedLands,
      creatures: groupedCreatures,
      planeswalkers: groupedPlaneswalkers,
      other: groupedOther,
      stats,
    }),
    [groupedLands, groupedCreatures, groupedPlaneswalkers, groupedOther, stats],
  )
}
