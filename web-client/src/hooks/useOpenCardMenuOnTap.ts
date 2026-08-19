import { useCallback } from 'react'
import { useGameStore } from '@/store/gameStore'
import type { EntityId } from '@/types'
import { useHasHover } from './useHasHover'

/**
 * Opens the action menu for a card that a tap would otherwise do nothing with — an opponent's
 * permanent, a land already played this turn, a spell on the stack.
 *
 * Without hover there is no other way to read a card, and the menu is where "View card" lives
 * (see `ActionMenu.showViewCard`), so on those devices every visible card has to be able to open
 * it. Returns `null` — meaning "leave the tap alone" — when a mouse is present, or while
 * spectating/replaying, where `GameBoard` doesn't mount the menu at all and selecting a card would
 * just light it up with nothing to follow.
 *
 * Tapping the same card again closes the menu, so the gesture is its own undo.
 */
export function useOpenCardMenuOnTap(): ((cardId: EntityId) => void) | null {
  const selectCard = useGameStore((state) => state.selectCard)
  const isSpectating = useGameStore((state) => state.spectatingState !== null)
  const hasHover = useHasHover()

  const open = useCallback(
    (cardId: EntityId) => {
      const alreadyOpen = useGameStore.getState().selectedCardId === cardId
      selectCard(alreadyOpen ? null : cardId)
    },
    [selectCard],
  )

  return hasHover || isSpectating ? null : open
}
