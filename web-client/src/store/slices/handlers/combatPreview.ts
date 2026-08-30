/**
 * How long the opponent's *streamed* combat declaration survives.
 *
 * While a player is picking attackers or blockers, their client streams the work-in-progress
 * selection to everyone else (`opponentAttackerTargets` / `opponentBlockerAssignments`) so the
 * table watches the arrows move in real time. Nothing in that stream says "I'm done" — it is
 * cancelled by the state update that supersedes it, and these two predicates are that rule.
 *
 * The bound is the declaration's own *step*. Keying it to the server's combat state alone is not
 * enough: a selection that never becomes a declaration — the attacker cancelled, or declared no
 * attackers at all — leaves `combat` null, which is the very condition the preview draws under,
 * so it would never be cleared and its arrows would stay painted for the rest of the game.
 * Multiplayer feels that worst, because there the attacker preview carries a per-attacker
 * defender and so draws full arrows rather than only the small direction chevrons.
 */
import { Step } from '@/types'

/**
 * Keep the attacker preview only while attackers are being declared and the declaration has not
 * landed yet — once it has, `combat` carries the real attackers and takes over.
 */
export function keepAttackerPreview(step: Step | null | undefined, hasCombat: boolean): boolean {
  return !hasCombat && step === Step.DECLARE_ATTACKERS
}

/**
 * Keep the blocker preview only while blockers are being declared. Unlike attackers, combat
 * already exists throughout (the attackers are on the battlefield), so the declaration landing
 * shows up as blockers appearing in it.
 */
export function keepBlockerPreview(
  step: Step | null | undefined,
  hasCombat: boolean,
  hasDeclaredBlockers: boolean,
): boolean {
  return hasCombat && !hasDeclaredBlockers && step === Step.DECLARE_BLOCKERS
}
