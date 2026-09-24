package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Way of the Mind Sculptor
 *
 * "If you removed two or more loyalty counters to activate it" reads the counters the activation's
 * cost actually removed — a [−2] or bigger, or a [−X] with X ≥ 2. A [+N] or [0] ability never
 * counts. The number is fixed once the cost is paid, so it is matched on the activation event.
 */
val WayOfTheMindSculptor = card("Way of the Mind Sculptor") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Mind Sculptor enters, empower Jace 5. (Put five loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Whenever you activate a loyalty ability, if you removed two or more loyalty counters to activate it, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(5)
    }

    triggeredAbility {
        trigger = Triggers.YouActivateLoyaltyAbilityRemovingAtLeast(2)
        effect = Effects.DrawCards(1)
        description = "Whenever you activate a loyalty ability, if you removed two or more loyalty " +
            "counters to activate it, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Josh Hass"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/5838af68-66c3-4fe8-ab89-0a1721b0cfeb.jpg?1789729565"
        inBooster = false
    }
}
