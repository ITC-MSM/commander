package com.wingedsheep.mtg.sets.definitions.mh2.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chatterstorm
 * {1}{G}
 * Sorcery
 *
 * Create a 1/1 green Squirrel creature token.
 * Storm
 */
val Chatterstorm = card("Chatterstorm") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Create a 1/1 green Squirrel creature token.\n" +
        "Storm (When you cast this spell, copy it for each spell cast before it this turn.)"

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Squirrel")
        )
    }

    keywords(Keyword.STORM)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Milivoj Ćeran"
        flavorText = "\"Friends, foragers, fuzzy fighters! Tonight we're after more than just acorns!\"\n—Larrel, Deep Forest hermit"
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b34f0ac1-6894-4761-b62c-b85d927acf09.jpg?1783926835"
    }
}
