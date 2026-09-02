/**
 * The DOM node a permanent is drawn as, on any battlefield the viewer can see.
 *
 * Scoped to the battlefield zones so it can't latch onto the same card id rendered somewhere else —
 * a hover preview, a zone browser, the graveyard pile it lands in after it dies.
 *
 * Overlays anchored to a permanent have to re-read this rather than remember a position: the board
 * reflows whenever a card leaves it, and the solver resizes every remaining card around the gap.
 * A remembered point doesn't just go stale, it becomes wrong — the neighbour that slid into the
 * vacated slot is now sitting under a number that belongs to a different card.
 */
export function battlefieldCardElement(cardId: string): Element | null {
  if (typeof document === 'undefined') return null
  return document.querySelector(`[data-zone$="-battlefield"] [data-card-id="${cardId}"]`)
}
