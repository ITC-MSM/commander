package com.wingedsheep.sdk.scripting.conditions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Condition: **"if you won"** — the rider on a *"Whenever you clash"* triggered ability
 * (CR 701.30d: a player wins a clash if the card they revealed has a higher mana value than every
 * other card revealed in that clash).
 *
 * This is the **triggered** half of clash's "if you win" template, and the sibling of the
 * **resolved** half. When a card performs the clash itself ("Clash with an opponent. If you win,
 * …") the win is a *pipeline result* and the rider is an ordinary
 * [com.wingedsheep.sdk.scripting.effects.Gate.DoAction] over
 * [com.wingedsheep.sdk.scripting.effects.SuccessCriterion.CollectionNonEmpty] reading the
 * clash's `storeWonAs` collection — see
 * [com.wingedsheep.sdk.dsl.MechanicPatterns.clash]. A `Whenever you clash` ability has no such
 * pipeline: the clash happened during some *other* object's resolution and is over by the time the
 * trigger goes on the stack ("This ability triggers after the clash ends"), so the outcome can only
 * reach the trigger as trigger context, captured from the `ClashedEvent` that fired it.
 *
 * "You" is the ability's controller because the event is emitted once per *clashing player* and
 * [com.wingedsheep.sdk.dsl.Triggers.WheneverYouClash] matches only the one about its own
 * controller. Per the Entangling Trap / Sylvan Echoes rulings that stays right when an opponent's
 * spell started the clash: you still clashed, and you can still have won.
 *
 * Resolution-only — a clash outcome is a historical fact about one event, not a state a
 * continuous effect could re-derive, so it reads `false` under projection. False for any trigger
 * that wasn't fired by a clash, and false on a tie or a draw from an empty library (nobody wins).
 *
 * Use [com.wingedsheep.sdk.dsl.Triggers.WheneverYouClashAndWin] instead when the *whole* ability is
 * conditional on winning ("Whenever you clash and win, …"): that filters the trigger itself and
 * never puts an ability on the stack. This condition is for the cards that do something either way
 * and only *part* of it depends on winning — Entangling Trap taps a creature regardless and merely
 * keeps it tapped on a win.
 */
@SerialName("YouWonTheClash")
@Serializable
data object YouWonTheClash : Condition {
    override val description: String = "if you won"
}
