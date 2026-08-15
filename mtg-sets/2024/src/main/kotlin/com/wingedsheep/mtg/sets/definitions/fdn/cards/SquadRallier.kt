package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Squad Rallier
 * {3}{W}
 * Creature — Human Scout
 * 3/4
 * {2}{W}: Look at the top four cards of your library. You may reveal a creature card with
 * power 2 or less from among them and put it into your hand. Put the rest on the bottom of
 * your library in a random order.
 */
val SquadRallier = card("Squad Rallier") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Scout"
    power = 3
    toughness = 4
    oracleText = "{2}{W}: Look at the top four cards of your library. You may reveal a creature card with power 2 or less from among them and put it into your hand. Put the rest on the bottom of your library in a random order."

    activatedAbility {
        cost = Costs.Mana("{2}{W}")
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.Creature.powerAtMost(2),
            prompt = "You may reveal a creature card with power 2 or less to put into your hand"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Edgar Sánchez Hidalgo"
        flavorText = "Between the call of the bugler and the shrill squawk of her mount, not a soul slept through morning muster."
        imageUri = "https://cards.scryfall.io/normal/front/6/5/65e1ee86-6f08-4aa0-bf63-ae12028ef080.jpg?1782689245"
    }
}
