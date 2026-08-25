package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Trumpeting Herd
 * {2}{G}{G}
 * Sorcery
 * Create a 3/3 green Elephant creature token.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * Unlike cascade and suspend, [Keyword.REBOUND] has a real consumer — `StackResolver` reads it off
 * `cardDef.keywords` when the spell resolves — so the bare keyword is the whole of the second line,
 * and the two Elephants arrive a turn apart from one [Effects.CreateToken]. The token's art comes
 * from the set's token sheet, so no image is spelled here.
 */
val TrumpetingHerd = card("Trumpeting Herd") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Create a 3/3 green Elephant creature token.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        effect = Effects.CreateToken(
            power = 3,
            toughness = 3,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elephant")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Lars Grant-West"
        flavorText = "An elephant never forgives."
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0f3b68e-f616-4687-bc2d-075165162cd1.jpg?1783933088"
    }
}
