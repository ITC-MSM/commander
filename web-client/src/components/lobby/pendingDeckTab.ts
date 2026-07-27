/**
 * "Open the deck picker on this tab" — handed across a lobby screen that has not mounted yet.
 *
 * Random pool is not a lobby setting. It is the deck picker's Random tab, whose empty deck list is
 * the server's own "roll me one" signal, so anything that *promises* a random pool has to move the
 * picker itself. Both things that promise one create the lobby from outside it:
 *
 * - the landing wizard, which has no lobby at all yet, and
 * - `useLobbyCommands.recreate`, which leaves the current lobby before creating the new one — and
 *   because leaving nulls the store slice synchronously while the create only sends a message, the
 *   lobby screen unmounts in between. Its `setDeckTab` was therefore a no-op on an unmounted
 *   component, which is why "Random pool ⇄" used to land on a lobby reading "Bring a deck".
 *
 * Module state rather than store state: it is consumed exactly once, by the next `LobbyScreen` to
 * mount, and nothing re-renders on it. Same shape as `loadLobbyId` in `store/slices/shared.ts`.
 */
import type { DeckPickerTab } from '../ui/DeckPicker'

let pending: DeckPickerTab | null = null

export function setPendingDeckTab(tab: DeckPickerTab): void {
  pending = tab
}

/** Read and clear. Returns undefined when nothing asked for a particular tab. */
export function takePendingDeckTab(): DeckPickerTab | undefined {
  const tab = pending
  pending = null
  return tab ?? undefined
}
