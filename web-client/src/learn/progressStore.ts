/**
 * Course progress. A mission is complete once its game reached a real end — won or lost; playing
 * it through is the point, not the result. A concede is not an end.
 *
 * Lives in localStorage for everyone (the course is aimed at someone who has not picked a name
 * yet), and on the account too once signed in: {@link syncLearnProgress} merges the two — a
 * mission finished anywhere counts, the play count is the larger — then writes the merge to both,
 * so "2 of 5" follows the player across devices without a guest ever losing anything.
 */
import { create } from 'zustand'
import { fetchLearnProgress, saveLearnProgress } from '@/api/account'
import { useAuthStore } from '@/store/authStore'
import { MISSIONS, type MissionId } from './missions'

const STORAGE_KEY = 'argentum.learn.progress'

export interface StoredProgress {
  completed: MissionId[]
  /** Mission id → how many times its game was finished, for the "play again" wording. */
  plays: Record<string, number>
}

const KNOWN = new Set<string>(MISSIONS.map((m) => m.id))

/** Read any JSON as progress, dropping mission ids this build does not know. */
export function parseProgress(raw: unknown): StoredProgress {
  if (!raw || typeof raw !== 'object') return { completed: [], plays: {} }
  const parsed = raw as Partial<StoredProgress>
  const plays: Record<string, number> = {}
  for (const [id, n] of Object.entries(parsed.plays ?? {})) {
    if (KNOWN.has(id) && typeof n === 'number' && n > 0) plays[id] = Math.floor(n)
  }
  return {
    completed: (Array.isArray(parsed.completed) ? parsed.completed : []).filter((id): id is MissionId => KNOWN.has(id)),
    plays,
  }
}

/** Union of finished missions (in course order), the larger play count per mission. */
export function mergeProgress(a: StoredProgress, b: StoredProgress): StoredProgress {
  const done = new Set<MissionId>([...a.completed, ...b.completed])
  const plays: Record<string, number> = {}
  for (const id of new Set([...Object.keys(a.plays), ...Object.keys(b.plays)])) {
    plays[id] = Math.max(a.plays[id] ?? 0, b.plays[id] ?? 0)
  }
  return { completed: MISSIONS.map((m) => m.id).filter((id) => done.has(id)), plays }
}

export function sameProgress(a: StoredProgress, b: StoredProgress): boolean {
  return JSON.stringify(a) === JSON.stringify(b)
}

function load(): StoredProgress {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? parseProgress(JSON.parse(raw)) : { completed: [], plays: {} }
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

/** Push to the account when signed in; a failure is silent — localStorage already has it. */
function pushToAccount(progress: StoredProgress) {
  if (useAuthStore.getState().status !== 'authenticated') return
  void saveLearnProgress(progress).catch(() => undefined)
}

interface LearnProgressState extends StoredProgress {
  /** A mission's game ended for real. Marks it complete and counts the play. */
  finish: (id: MissionId) => void
  reset: () => void
  /** Replace the whole document — what a sync does after merging. */
  replace: (progress: StoredProgress) => void
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
    pushToAccount(next)
    set(next)
  },
  reset: () => {
    const next: StoredProgress = { completed: [], plays: {} }
    save(next)
    pushToAccount(next)
    set(next)
  },
  replace: (progress) => {
    save(progress)
    set(progress)
  },
}))

let syncing: Promise<void> | null = null

/**
 * Merge this browser's progress with the signed-in account's and store the result in both places.
 * No-op for guests. Safe to call often (the course home, the end of a mission): one sync runs at a
 * time and a network failure leaves the local copy untouched.
 */
export function syncLearnProgress(): Promise<void> {
  if (useAuthStore.getState().status !== 'authenticated') return Promise.resolve()
  if (syncing) return syncing
  syncing = (async () => {
    try {
      const remote = parseProgress(await fetchLearnProgress())
      const local = { completed: useLearnProgress.getState().completed, plays: useLearnProgress.getState().plays }
      const merged = mergeProgress(local, remote)
      if (!sameProgress(merged, local)) useLearnProgress.getState().replace(merged)
      if (!sameProgress(merged, remote)) await saveLearnProgress(merged)
    } catch {
      // Offline, or the server has no accounts: the local copy stands.
    } finally {
      syncing = null
    }
  })()
  return syncing
}

/** The first mission not yet completed — where "Continue" goes. Undefined once the course is done. */
export function nextIncomplete(completed: readonly MissionId[]): MissionId | undefined {
  return MISSIONS.find((m) => !completed.includes(m.id))?.id
}

/** True once any mission has been finished in this browser — what decides the landing pointer's wording. */
export function hasStarted(progress: Pick<StoredProgress, 'completed'>): boolean {
  return progress.completed.length > 0
}
