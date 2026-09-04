package com.wingedsheep.sdk.scripting.effects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Pipeline collection holding the clashing player's revealed card — empty unless they won. */
const val CLASH_WON = "clashWon"

/** Pipeline collection holding the clashing player's own revealed card. */
const val CLASH_YOURS = "clashYours"

/** Pipeline collection holding the chosen opponent's revealed card. */
const val CLASH_THEIRS = "clashTheirs"

/**
 * Clash with the chosen opponent (CR 701.30) as a single compact node.
 *
 * This is a *macro effect* in the sense of [ScryEffect]: a serializable marker that expands at
 * execution time into a Gather → Select → Move pipeline built from the ordinary library
 * primitives, so it adds no new gather/select/move logic of its own. What it does add is the one
 * thing the pipeline can't express — **who decides first**. CR 701.30c orders the two top-or-bottom
 * decisions in APNAP order, which depends on whose turn it is, and a `CompositeEffect` has a fixed
 * step order. The engine's clash executor therefore builds the pipeline against the live state and
 * emits the two [SelectFromCollectionEffect] steps active-player-first.
 *
 * The clash proper is only half the printed template. Every clash card reads "Clash with an
 * opponent. **If you win,** …", which is a
 * [com.wingedsheep.sdk.scripting.effects.GatedEffect] over a
 * [com.wingedsheep.sdk.scripting.effects.Gate.DoAction] whose
 * [com.wingedsheep.sdk.scripting.effects.SuccessCriterion.CollectionNonEmpty] reads [storeWonAs] —
 * see [com.wingedsheep.sdk.dsl.MechanicPatterns.clash], which is what cards actually author.
 * Winning is a *pipeline result*, not a new gate kind, so the existing action-outcome gate and its
 * pause/resume machinery carry the whole card.
 *
 * **Requires a preceding [ChooseOpponentForSourceEffect]** — "clash with an opponent" is a choice,
 * not a target (CR 701.30b), and the chosen opponent must be the same player for the gather and
 * the decision, which [com.wingedsheep.sdk.scripting.references.Player.ChosenOpponent] guarantees
 * and a bare `Chooser.Opponent` would not (it re-picks per step). `MechanicPatterns.clash` prefixes
 * it. With a single opponent the choice is forced and promptless, so two-player games see no extra
 * decision.
 *
 * The nearest existing primitive is [ExileTopCardContestEffect] (Timesifter), and this deliberately
 * copies its *interface* rather than its mechanism: answer "who won" into a pipeline collection,
 * leave that collection empty when nobody did, and let the card compose its own payoff off it. The
 * mechanisms genuinely differ — the contest **exiles** and re-runs tied players until one is alone,
 * while a clash **reveals**, leaves both cards in their libraries under their owners' control, and
 * settles a tie as "nobody won" — so there is nothing to share but the shape. Matching the shape is
 * what keeps the two reading alike, and what makes the empty-library and no-winner cases behave the
 * same way in both.
 *
 * @property storeWonAs Pipeline-storage collection that receives the clashing player's revealed
 *   card **iff they won** the clash, and stays empty otherwise. Reading a win as "is this
 *   collection non-empty" is what lets the printed "if you win" rider be an ordinary
 *   [SuccessCriterion.CollectionNonEmpty] instead of a bespoke gate.
 */
@SerialName("Clash")
@Serializable
data class ClashEffect(
    val storeWonAs: String = CLASH_WON
) : Effect {
    override val description: String = "Clash with an opponent"
}

/**
 * Tail of the clash pipeline: score the clash and emit the `ClashedEvent`.
 *
 * The clash twin of [EmitScriedEventEffect], and the same shape — an internal marker the mechanic's
 * pipeline appends, never authored by a card. It does two things the surrounding gather/select/move
 * steps can't:
 *
 *  - **Scores the clash (CR 701.30d).** A player wins iff the card they revealed has a *strictly*
 *    greater mana value than every other card revealed in that clash. Ties win for nobody, and an
 *    empty library reveals nothing — a player who revealed no card can't have the greatest mana
 *    value, so they never win, while their opponent still can. The clasher's revealed card is
 *    written to [storeWonAs] on a win and nothing is written on a loss, which is the flag the
 *    printed "if you win" rider gates on.
 *  - **Fires "Whenever you clash" triggers**, once per clashing *player* — both of them. Per the
 *    Entangling Trap / Sylvan Echoes rulings a clash you did not initiate still triggers your own
 *    clash payoffs, and you can win a clash an opponent started, so the event names both
 *    participants and carries who (if anyone) won.
 *
 * Scoring happens here, after the top/bottom moves, rather than mid-pipeline: mana value is a
 * printed characteristic that reads the same from the library as from the top of it, so the moves
 * can't change the answer, and emitting the event last is what CR requires ("this ability triggers
 * after the clash ends").
 *
 * @property yourCollection Gather collection holding the clashing player's revealed card.
 * @property theirCollection Gather collection holding the chosen opponent's revealed card.
 * @property storeWonAs Collection to write the clashing player's card into on a win.
 */
@SerialName("EmitClashedEvent")
@Serializable
data class EmitClashedEventEffect(
    val yourCollection: String = CLASH_YOURS,
    val theirCollection: String = CLASH_THEIRS,
    val storeWonAs: String = CLASH_WON
) : Effect {
    // Intentionally blank: this is an internal pipeline tail with no player-facing text.
    override val description: String = ""
}
