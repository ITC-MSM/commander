package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Call to the Feast
 * {2}{W}{B}
 * Sorcery
 *
 * Create three 1/1 white Vampire creature tokens with lifelink.
 */
val CallToTheFeast = card("Call to the Feast") {
    manaCost = "{2}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Sorcery"
    oracleText = "Create three 1/1 white Vampire creature tokens with lifelink."

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Vampire"),
            keywords = setOf(Keyword.LIFELINK),
            count = 3,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Yongjae Choi"
        flavorText = "By the law of church and crown, vampires feed only on the blood of the guilty—those declared heretics, rebels, or enemies of war."
        imageUri = "https://cards.scryfall.io/normal/front/f/7/f7226de1-0e05-4baf-8c2f-54297fee43c1.jpg"
    }
}
