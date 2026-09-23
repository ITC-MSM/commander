package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val WayOfTheWildspeaker = card("Way of the Wildspeaker") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Wildspeaker enters, empower Jace 7. (Put seven loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Planeswalkers you control have \"[−4]: Create a 4/4 green Beast creature token with trample.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(7)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(-4) {
                effect = Effects.CreateToken(
                    power = 4,
                    toughness = 4,
                    colors = setOf(Color.GREEN),
                    creatureTypes = setOf("Beast"),
                    keywords = setOf(Keyword.TRAMPLE),
                    imageUri = "https://cards.scryfall.io/normal/front/8/5/859bda9a-fa90-4ad3-b0c1-6fc62e27c12f.jpg?1789736256"
                )
                description = "Create a 4/4 green Beast creature token with trample."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "268"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a252cb01-537b-4afe-9abc-81a98c4a1439.jpg?1789729602"
        inBooster = false
    }
}
