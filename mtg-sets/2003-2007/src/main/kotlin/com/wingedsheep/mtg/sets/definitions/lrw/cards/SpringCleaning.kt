package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

val SpringCleaning = card("Spring Cleaning") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target enchantment. Clash with an opponent. If you win, destroy all enchantments your opponents control. (Each clashing player reveals the top card of their library, then puts that card on their choice of the top or bottom. A player wins if their card had a greater mana value.)"

    spell {
        val enchantment = target("target enchantment", Targets.Enchantment)
        effect = Effects.Destroy(enchantment).then(
            Patterns.Mechanic.clash(
                Effects.DestroyAll(GameObjectFilter.Enchantment.opponentControls())
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "236"
        artist = "Michael Sutfin"
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dc20ef13-e97f-454c-8295-7bb8a7c4e4be.jpg?1783942857"
        ruling("2013-09-20", "If you win the clash and the targeted enchantment is controlled by an opponent, it will be destroyed a second time if it's still on the battlefield (for instance, if it somehow regenerated).")
        ruling("2007-10-01", "If you win the clash, you destroy all enchantments all of your opponents control, not just enchantments controlled by the opponent you clashed with.")
    }
}
