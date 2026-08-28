/**
 * Things the player does *to the app* that leave no trace in the game state — opening the log,
 * pressing Undo. The board components mark them with one call each, and course objectives read
 * them like any other fact. In-memory only: a new game is a full page navigation, so a fresh
 * document starts with nothing marked, and a board remount mid-game keeps what was marked.
 */
import { create } from 'zustand'

export type LearnSignal = 'logOpened' | 'undoUsed'

interface LearnSignalsState {
  marked: ReadonlySet<LearnSignal>
  mark: (signal: LearnSignal) => void
}

export const useLearnSignals = create<LearnSignalsState>((set, get) => ({
  marked: new Set(),
  mark: (signal) => {
    if (get().marked.has(signal)) return
    set({ marked: new Set([...get().marked, signal]) })
  },
}))

/** For components that just want to say "this happened" without subscribing. */
export function markLearnSignal(signal: LearnSignal) {
  useLearnSignals.getState().mark(signal)
}
