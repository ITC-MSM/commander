package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Transmute (Comprehensive Rules 702.53): discard from hand as a sorcery to search
 * for a card with the discarded card's mana value, reveal it, and shuffle.
 * Uses the ordinary zone-activation and library-search pipelines.
 *
 * Declare the card's mana cost before calling this helper. The hand-zone mana value
 * is fixed in the definition: a discarded card that is reanimated and copied in
 * response is a new object and must not change this ability's search criterion.
 * Cost reductions do not change mana value, and X outside the stack counts as zero.
 */
fun CardBuilder.transmute(cost: String) {
    val discardedManaValue = ManaCost.parse(manaCost).cmc
    activatedAbility {
        this.cost = Costs.Composite(Costs.Mana(cost), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        timing = TimingRule.SorcerySpeed
        description = "Transmute $cost — Discard this card to search for a card with the same mana value."
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.manaValue(discardedManaValue),
            reveal = true
        )
    }
}
