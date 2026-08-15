package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.convergeEntersWithCounters
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Wildgrowth Archaic
 * {2/G}{2/G}
 * Creature — Avatar
 * 0/0
 *
 * Trample, reach
 * Converge — This creature enters with a +1/+1 counter on it for each color of mana spent to cast it.
 * Whenever you cast a creature spell, that creature enters with X additional +1/+1 counters on it,
 * where X is the number of colors of mana spent to cast it.
 *
 * Two `EntersWithDynamicCounters` replacement effects, both fed by [DynamicAmounts.colorsOfManaSpent]
 * (`DynamicAmount.DistinctColorsManaSpent`), which reads the *entering object's* recorded payment:
 *
 *  1. **Self** — the printed Converge enters-with-counters (the Archaic cycle), via
 *     [convergeEntersWithCounters]. With a 0/0 base, the colour count is also what keeps the body
 *     alive: all-colourless payment leaves a 0/0 that dies as a state-based action.
 *  2. **Other creatures you cast** — `EntersWithDynamicCounters(otherOnly = true)` on creatures you
 *     control entering the battlefield. The oracle text triggers on "whenever you cast a creature
 *     spell" and the additional counters scale with "the number of colors of mana spent to cast
 *     **it**" (= that creature spell), so the count is about the *entering* creature, not Wildgrowth.
 *     The engine's global enters-with path evaluates the count against the entering object (so
 *     `DistinctColorsManaSpent` reads the new creature's own cast), and a permanent that entered
 *     without being cast (token / reanimation) spent no mana → 0 colours → no extra counters, which
 *     matches the "whenever you **cast**" wording.
 */
val WildgrowthArchaic = card("Wildgrowth Archaic") {
    manaCost = "{2/G}{2/G}"
    colorIdentity = "G"
    typeLine = "Creature — Avatar"
    power = 0
    toughness = 0
    oracleText = "Trample, reach\n" +
        "Converge — This creature enters with a +1/+1 counter on it for each color of mana spent " +
        "to cast it.\n" +
        "Whenever you cast a creature spell, that creature enters with X additional +1/+1 counters " +
        "on it, where X is the number of colors of mana spent to cast it."

    keywords(Keyword.TRAMPLE, Keyword.REACH)

    // 1. Converge — this creature's own enters-with counters.
    convergeEntersWithCounters()

    // 2. Other creatures you cast enter with additional +1/+1 counters equal to the colours of mana
    //    spent on *their* cast. otherOnly = true routes this through the battlefield-scan global
    //    enters-with path, which evaluates the count against the entering creature.
    replacementEffect(
        EntersWithDynamicCounters(
            count = DynamicAmounts.colorsOfManaSpent(),
            otherOnly = true,
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Creature.youControl(),
                to = Zone.BATTLEFIELD,
            ),
        ),
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "168"
        artist = "Loïc Canavaggia"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0e6e2188-7203-4d10-a838-27233f283cd5.jpg?1775938149"
    }
}
