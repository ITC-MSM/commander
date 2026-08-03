package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.EntersWithRiot
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter

/** Reminder text for riot, matching the printed wording. */
const val RIOT_REMINDER: String =
    "Riot (This creature enters with your choice of a +1/+1 counter or haste.)"

/**
 * Add Riot (CR 702.136, Ravnica Allegiance) to this card: "You may have this permanent enter with
 * an additional +1/+1 counter on it. If you don't, it gains haste."
 *
 * The keyword is display-only; the behavior is the [EntersWithRiot] replacement, which the engine
 * resolves as the permanent enters — one decision per instance, applied immediately. Riot is a
 * replacement effect, not a trigger: the choice can't be responded to, and the haste it grants is
 * indefinite rather than until end of turn.
 *
 * Multiple instances work separately (CR 702.136b) — calling this twice, or pairing a printed riot
 * with one granted by [riotFor], gives the permanent two independent choices.
 */
fun CardBuilder.riot() {
    keywordSet.add(Keyword.RIOT)
    replacementEffect(EntersWithRiot(selfOnly = true))
}

/**
 * Grant riot to *other* permanents matching [filter] as they enter — "Other Spiders you control
 * have riot" (Spider-Punk), "Nontoken creatures you control have riot" (Rhythm of the Wild).
 *
 * The source itself is never covered; add [riot] as well when the card also has riot printed on it,
 * as Spider-Punk does. The grant rides the same `otherOnly` + `appliesTo` global rail the
 * enters-with counters/keywords siblings use: it is stamped into the granter's battlefield
 * replacement-source component and consulted as each matching permanent enters.
 */
fun CardBuilder.riotFor(filter: GameObjectFilter) {
    replacementEffect(
        EntersWithRiot(
            otherOnly = true,
            appliesTo = EventPattern.ZoneChangeEvent(filter = filter, to = Zone.BATTLEFIELD),
        )
    )
}
