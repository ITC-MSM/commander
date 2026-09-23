package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PlayAdditionalLandsEffect

val WayOfTheParadox = card("Way of the Paradox") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Paradox enters, empower Jace 5. (Put five loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Whenever you activate a loyalty ability, you gain 1 life. You may play an additional land this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(5)
    }

    triggeredAbility {
        trigger = Triggers.YouActivateLoyaltyAbility
        effect = Effects.Composite(
            Effects.GainLife(1),
            PlayAdditionalLandsEffect(count = 1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "267"
        artist = "Josh Hass"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98dc5470-507a-4364-8480-42607255e56c.jpg?1789729606"
        inBooster = false
    }
}
