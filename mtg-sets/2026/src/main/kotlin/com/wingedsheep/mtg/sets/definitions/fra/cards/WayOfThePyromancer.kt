package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val WayOfThePyromancer = card("Way of the Pyromancer") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Pyromancer enters, empower Jace 2. (Put two loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Planeswalkers you control have \"[+1]: Add {R}.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(2)
    }

    // A loyalty ability is never a mana ability (CR 605.1a), so the granted +1 uses the stack.
    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(+1) {
                effect = Effects.AddMana(Color.RED)
                description = "Add {R}."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "254"
        artist = "Andrey Kuzinskiy"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1a00020-7c14-4503-a057-5763704bb83e.jpg?1788878311"
        inBooster = false
    }
}
