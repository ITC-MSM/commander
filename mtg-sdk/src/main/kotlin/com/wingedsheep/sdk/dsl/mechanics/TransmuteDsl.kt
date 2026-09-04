package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Transmute (Comprehensive Rules 702.53): discard from hand as a sorcery to search
 * for a card with the discarded card's mana value, reveal it, and shuffle.
 * Uses the ordinary zone-activation and library-search pipelines.
 */
fun CardBuilder.transmute(cost: String) {
    activatedAbility {
        this.cost = Costs.Composite(Costs.Mana(cost), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        timing = TimingRule.SorcerySpeed
        description = "Transmute $cost — Discard this card to search for a card with the same mana value."
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.manaValueEqualsDynamic(DynamicAmounts.sourceManaValue()),
            reveal = true
        )
    }
}
