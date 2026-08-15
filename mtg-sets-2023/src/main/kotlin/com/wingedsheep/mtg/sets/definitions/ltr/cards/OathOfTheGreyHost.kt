package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Oath of the Grey Host
 * {3}{B}
 * Enchantment — Saga
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — You and target opponent each create a Food token.
 * II — Each opponent loses 3 life. Create a Treasure token.
 * III — Create three tapped 1/1 white Spirit creature tokens with flying.
 */
val OathOfTheGreyHost = card("Oath of the Grey Host") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — You and target opponent each create a Food token.\n" +
        "II — Each opponent loses 3 life. Create a Treasure token.\n" +
        "III — Create three tapped 1/1 white Spirit creature tokens with flying."

    sagaChapter(1) {
        val opponent = target("target opponent", Targets.Opponent)
        effect = Effects.CreateFood()
            .then(Effects.CreateFood(controller = opponent))
    }

    sagaChapter(2) {
        effect = Effects.LoseLife(3, EffectTarget.PlayerRef(Player.EachOpponent))
            .then(Effects.CreateTreasure())
    }

    sagaChapter(3) {
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(3),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            tapped = true,
            imageUri = "https://cards.scryfall.io/normal/front/6/a/6a780abd-f276-40d3-b2af-d2e47d858d3d.jpg?1689938345"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "101"
        artist = "Miklós Ligeti"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a780abd-f276-40d3-b2af-d2e47d858d3d.jpg?1689938345"
    }
}
