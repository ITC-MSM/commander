/**
 * Auto-pick for a "tap creatures with total power N" cost — Crew N (CR 702.122a), Saddle N
 * (CR 702.171a) and Teamwork N (CR 702.194a).
 *
 * This is a *suggestion*, never a rules decision: the server enumerated the candidates, the server
 * validates the submitted set, and the player can edit the pick freely afterwards. What it encodes
 * is the one thing the numbers alone don't say — paying this cost taps the creatures, and a tapped
 * creature can't be declared as an attacker, so the cheapest way to pay is with the creatures that
 * weren't going to attack anyway.
 */
import type { EntityId } from '@/types'

export interface PowerCandidate {
  readonly entityId: EntityId
  readonly power: number
  /** Server's verdict on whether this creature could attack right now. Absent = assume it could. */
  readonly canAttack?: boolean
}

/** Sum the contribution of the given selection. */
export function totalPowerOf(
  candidates: readonly PowerCandidate[],
  selected: readonly EntityId[]
): number {
  let total = 0
  for (const id of selected) {
    const candidate = candidates.find((c) => c.entityId === id)
    if (candidate) total += candidate.power
  }
  return total
}

/**
 * Take from [pool] until [needed] is covered, preferring the single creature that finishes the job
 * with the least overshoot and otherwise spending the largest first so the fewest creatures tap.
 * Returns what was taken and what is still missing (0 when the pool covered it).
 *
 * Creatures contributing 0 or less are skipped outright — they can never close the gap, and
 * including one would make the loop spin.
 */
function takeFrom(
  pool: readonly PowerCandidate[],
  needed: number
): { chosen: PowerCandidate[]; missing: number } {
  let remaining = pool.filter((c) => c.power > 0)
  const chosen: PowerCandidate[] = []
  let missing = needed

  while (missing > 0 && remaining.length > 0) {
    const finishers = remaining.filter((c) => c.power >= missing)
    if (finishers.length > 0) {
      // Smallest creature that covers the rest: one tap, least power wasted.
      const best = finishers.reduce((a, b) => (b.power < a.power ? b : a))
      chosen.push(best)
      missing = 0
      break
    }
    // Nothing covers it alone — spend the biggest so the gap closes in the fewest taps.
    const biggest = remaining.reduce((a, b) => (b.power > a.power ? b : a))
    chosen.push(biggest)
    missing -= biggest.power
    remaining = remaining.filter((c) => c !== biggest)
  }

  return { chosen, missing }
}

/**
 * Choose creatures whose total contribution reaches [required], spending the least combat value.
 *
 * Creatures the server says can't attack are spent first (tapping them costs nothing this turn);
 * only if they don't cover the cost does the pick reach into the creatures that could have
 * attacked. Returns the ids in selection order. If the candidates can't cover [required] at all,
 * returns everything that helps — the confirm button stays disabled and the player sees how short
 * the board is.
 */
export function autoSelectForPower(
  candidates: readonly PowerCandidate[],
  required: number
): EntityId[] {
  if (required <= 0) return []

  const spare = candidates.filter((c) => c.canAttack === false)
  const attackers = candidates.filter((c) => c.canAttack !== false)

  const fromSpare = takeFrom(spare, required)
  if (fromSpare.missing <= 0) return fromSpare.chosen.map((c) => c.entityId)

  const fromAttackers = takeFrom(attackers, fromSpare.missing)
  return [...fromSpare.chosen, ...fromAttackers.chosen].map((c) => c.entityId)
}
