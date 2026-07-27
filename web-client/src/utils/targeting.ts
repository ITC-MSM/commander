import type { ChooseTargetsDecision } from '@/types'

/**
 * A ChooseTargetsDecision that the lone-player click-to-submit path can satisfy: exactly one
 * target requirement, and that requirement wants at most one target.
 *
 * Only such a decision may be answered by clicking a single player's life orb and submitting
 * `{ 0: [playerId] }` immediately (see PlayerTargetingUI, LifeDisplay, OpponentRail). A
 * multi-target player slot — e.g. Parker Luck's "two target players" (maxTargets = 2) — is NOT
 * lone: it must accumulate orb picks through decisionSelectionState with a Confirm step
 * (BattlefieldTargetingUI), because immediately submitting one player would fail the server's
 * minimum-targets check and strand the player with no way to pick the second.
 */
export function isLoneTargetRequirement(decision: ChooseTargetsDecision): boolean {
  if (decision.targetRequirements.length !== 1) return false
  return (decision.targetRequirements[0]?.maxTargets ?? 1) <= 1
}
