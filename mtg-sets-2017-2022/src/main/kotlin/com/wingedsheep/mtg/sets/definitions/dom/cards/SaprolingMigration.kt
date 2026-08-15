package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Saproling Migration
 * {1}{G}
 * Sorcery
 * Kicker {4}
 * Create two 1/1 green Saproling creature tokens. If this spell was kicked,
 * create four of those tokens instead.
 */
val SaprolingMigration = card("Saproling Migration") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Kicker {4}\nCreate two 1/1 green Saproling creature tokens. If this spell was kicked, create four of those tokens instead."

    keywordAbility(KeywordAbility.kicker("{4}"))

    spell {
        effect = ConditionalEffect(
            condition = WasKicked,
            effect = CreateTokenEffect(
                count = 4,
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Saproling"),
                imageUri = "https://cards.scryfall.io/normal/front/5/3/5371de1b-db33-4db4-a518-e35c71aa72b7.jpg?1562702067"
            ),
            elseEffect = CreateTokenEffect(
                count = 2,
                power = 1,
                toughness = 1,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Saproling"),
                imageUri = "https://cards.scryfall.io/normal/front/5/3/5371de1b-db33-4db4-a518-e35c71aa72b7.jpg?1562702067"
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Christine Choi"
        flavorText = "Thallids herd saprolings from place to place in search of detritus to feed them."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/2578d25e-5f8d-42ff-90ce-4e7d100cbbb6.jpg?1562732840"
    }
}
