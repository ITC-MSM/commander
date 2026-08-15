package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shredder's Revenge
 * {2}{B}
 * Sorcery
 *
 * Choose one —
 * • Target player discards two cards.
 * • Target player draws two cards and loses 2 life.
 */
val ShreddersRevenge = card("Shredder's Revenge") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n• Target player discards two cards.\n• Target player draws two cards and loses 2 life."

    spell {
        modal(chooseCount = 1) {
            mode("Target player discards two cards") {
                val player = target("target player", Targets.Player)
                effect = Effects.Discard(2, player)
            }
            mode("Target player draws two cards and loses 2 life") {
                val player = target("target player", Targets.Player)
                effect = Effects.DrawCards(2, player)
                    .then(Effects.LoseLife(2, player))
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "Svetlin Velinov"
        flavorText = "\"Tonight I dine on turtle soup.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72fadf47-2e0b-4b24-b1bc-7c86780716b6.jpg?1771342364"
    }
}
