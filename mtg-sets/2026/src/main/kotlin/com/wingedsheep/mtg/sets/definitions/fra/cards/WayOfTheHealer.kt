package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val WayOfTheHealer = card("Way of the Healer") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Healer enters, empower Jace 5. (Put five loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Planeswalkers you control have \"[−2]: Create a 2/2 colorless Wizard Soldier creature token named Cadet. Surveil 1.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(5)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(-2) {
                effect = Effects.CreateToken(
                    power = 2,
                    toughness = 2,
                    creatureTypes = setOf("Wizard", "Soldier"),
                    name = "Cadet",
                    imageUri = "https://cards.scryfall.io/normal/front/8/f/8f4534d8-2783-484f-8ebf-a47b1cc4c6df.jpg?1789734318"
                ).then(Patterns.Library.surveil(1))
                description = "Create a 2/2 colorless Wizard Soldier creature token named Cadet. Surveil 1."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "207"
        artist = "Pauline Voss"
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50326a2a-7e10-464b-a97e-e880bda0558c.jpg?1789729503"
        inBooster = false
    }
}
