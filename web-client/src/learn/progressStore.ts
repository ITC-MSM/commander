/**
 * Course progress, kept in this browser. A mission is complete once its game reached its end —
 * won or lost; playing it through is the point, not the result.
 *
 * localStorage rather than the account: the course is aimed at someone who has not picked a name
 * yet, let alone signed in, and losing "2 of 4" on a new device is no real loss.
 */
import { create } from 'zustand'
import { MISSIONS, type MissionId } from './missions'

const STORAGE_KEY = 'argentum.learn.progress'

export interface StoredProgress {
  completed: MissionId[]
  /** Mission id → how many times its game was finished, for the "play again" wording. */
  plays: Record<string, number>
}

function load(): StoredProgress {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return { completed: [], plays: {} }
    const parsed = JSON.parse(raw) as Partial<StoredProgress>
    const known = new Set<string>(MISSIONS.map((m) => m.id))
    return {
      completed: (parsed.completed ?? []).filter((id): id is MissionId => known.has(id)),
      plays: parsed.plays ?? {},
    }
  } catch {
    return { completed: [], plays: {} }
  }
}

function save(progress: StoredProgress) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(progress))
  } catch {
    // Private mode / quota — progress just does not persist this session.
  }
}

interface LearnProgressState extends StoredProgress {
  /** A mission's game ended. Marks it complete and counts the play. */
  finish: (id: MissionId) => void
  reset: () => void
}

export const useLearnProgress = create<LearnProgressState>((set, get) => ({
  ...load(),
  finish: (id) => {
    const { completed, plays } = get()
    const next: StoredProgress = {
      completed: completed.includes(id) ? completed : [...completed, id],
      plays: { ...plays, [id]: (plays[id] ?? 0) + 1 },
    }
    save(next)
    set(next)
  },
  reset: () => {
    const next: StoredProgress = { completed: [], plays: {} }
    save(next)
    set(next)
  },
}))

/** The first mission not yet completed — where "Continue" goes. Undefined once the course is done. */
export function nextIncomplete(completed: readonly MissionId[]): MissionId | undefined {
  return MISSIONS.find((m) => !completed.includes(m.id))?.id
}

/** True once any mission has been finished in this browser — what decides the landing pointer's wording. */
export function hasStarted(progress: Pick<StoredProgress, 'completed'>): boolean {
  return progress.completed.length > 0
}
