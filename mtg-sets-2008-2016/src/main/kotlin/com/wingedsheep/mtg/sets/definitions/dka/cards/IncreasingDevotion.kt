package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Increasing Devotion
 * {3}{W}{W}
 * Sorcery
 * Create five 1/1 white Human creature tokens. If this spell was cast from a graveyard,
 * create ten of those tokens instead.
 * Flashback {7}{W}{W}
 */
val IncreasingDevotion = card("Increasing Devotion") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText =
        "Create five 1/1 white Human creature tokens. If this spell was cast from a graveyard, " +
            "create ten of those tokens instead.\n" +
            "Flashback {7}{W}{W} (You may cast this card from your graveyard for its flashback cost. " +
            "Then exile it.)"

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.Conditional(
                condition = Conditions.WasCastFromGraveyard,
                ifTrue = DynamicAmount.Fixed(10),
                ifFalse = DynamicAmount.Fixed(5),
            ),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human"),
        )
    }

    keywordAbility(KeywordAbility.flashback("{7}{W}{W}"))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "11"
        artist = "Daniel Ljunggren"
        imageUri =
            "https://cards.scryfall.io/normal/front/8/7/87b5de81-65a6-4a74-8a71-767b92e89e91.jpg?1783941024"
    }
}
