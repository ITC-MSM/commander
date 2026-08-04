import type { ChooseTargetsDecision, ClientCard, ClientGameState, EntityId } from '@/types'
import { ZoneType } from '@/types'

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

/** Zones whose cards are only reachable through a pile picker — never clickable on the board. */
const PILE_ZONES: ReadonlySet<ZoneType> = new Set([ZoneType.GRAVEYARD, ZoneType.EXILE])

/**
 * The cards for one target requirement when *all* of its legal targets sit in a pile zone
 * (graveyard or exile), else `null`.
 *
 * A pile isn't individually clickable on the board, so such a requirement has to be collected by
 * [GraveyardTargetingUI]; anything else (battlefield permanents, players, stack objects) is picked
 * by clicking the board through `decisionSelectionState` ([BattlefieldTargetingUI]). This is
 * evaluated **per requirement**, not once for the whole decision: The Spot, Living Portal's ETB
 * asks for a battlefield permanent *and* a graveyard card, so the two slots route to different UIs.
 *
 * All-or-nothing on purpose — one battlefield target in the set means the board path must handle
 * the slot, and a target the client can't resolve (missing card, null zone) is treated as
 * not-a-pile so the requirement still reaches a UI that can show *something*.
 */
export function getPileTargetCards(
  legalTargets: readonly EntityId[],
  cards: ClientGameState['cards'] | undefined,
): ClientCard[] | null {
  if (!cards || legalTargets.length === 0) return null

  const pileCards: ClientCard[] = []
  for (const targetId of legalTargets) {
    const card = cards[targetId]
    const zoneType = card?.zone?.zoneType
    if (!card || !zoneType || !PILE_ZONES.has(zoneType)) return null
    pileCards.push(card)
  }
  return pileCards
}

/**
 * Boilerplate that hangs off the *exile* verb in O-Ring style effects — `ExileUntilLeavesEffect`
 * renders "Exile … until this permanent leaves the battlefield". It names the battlefield without
 * the effect ever putting anything there, so it must not read as reanimation (The Spot, Living
 * Portal composes two of them and would otherwise offer "Put onto Battlefield" for a card it exiles).
 */
const LEAVES_BATTLEFIELD_BOILERPLATE = 'leaves the battlefield'

/** What a pile-targeting requirement will do to the picked cards, in button and sentence form. */
export interface PileAction {
  /** Label for the confirm button on an optional pile target. */
  confirmText: string
  /** Verb phrase for the helper sentence: "Choose a card to <verb>." */
  verb: string
}

/**
 * Derive the action wording for a pile-targeting requirement from the decision's effect hint:
 * "Exile card in a graveyard" → Exile; "Shuffle … into its owner's library" → Shuffle into Library;
 * "Put … onto the battlefield" → Put onto Battlefield.
 *
 * Effects can be wrapped (ForEachTargetEffect, CompositeEffect, …) so the keyword may not be at the
 * start — match anywhere in the hint. "Return to Hand" is only the fallback, so reanimation effects
 * (Shark Shredder) must be detected explicitly or they'd mislabel as returning the opponent's card
 * to hand. [LEAVES_BATTLEFIELD_BOILERPLATE] is discounted first; every other mention of the
 * battlefield is a destination.
 */
export function derivePileAction(effectHint: string | null | undefined): PileAction {
  const hint = (effectHint?.toLowerCase() ?? '').replaceAll(LEAVES_BATTLEFIELD_BOILERPLATE, '')

  if (hint.includes('battlefield')) {
    return { confirmText: 'Put onto Battlefield', verb: 'put onto the battlefield' }
  }
  if (hint.includes('shuffle') && hint.includes('library')) {
    return { confirmText: 'Shuffle into Library', verb: 'shuffle into your library' }
  }
  if (hint.includes('exile')) {
    return { confirmText: 'Exile', verb: 'exile' }
  }
  return { confirmText: 'Return to Hand', verb: 'return to your hand' }
}
