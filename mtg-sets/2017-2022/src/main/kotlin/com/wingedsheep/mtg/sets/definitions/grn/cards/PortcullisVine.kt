package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Portcullis Vine
 * {G}
 * Creature — Plant Wall
 * 0/3
 * Defender (This creature can't attack.)
 * {2}, {T}, Sacrifice a creature with defender: Draw a card.
 */
val PortcullisVine = card("Portcullis Vine") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wall"
    oracleText = "Defender (This creature can't attack.)\n" +
        "{2}, {T}, Sacrifice a creature with defender: Draw a card."
    power = 0
    toughness = 3

    keywords(Keyword.DEFENDER)
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Creature.withKeyword(Keyword.DEFENDER))
        )
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "James Paick"
        flavorText = "Nature's way of saying \"take the long way home.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b6dab62-b747-4d76-9fa8-4914582fc212.jpg?1783934148"
    }
}
