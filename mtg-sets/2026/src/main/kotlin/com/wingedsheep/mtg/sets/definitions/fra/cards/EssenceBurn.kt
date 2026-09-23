package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

val EssenceBurn = card("Essence Burn") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Essence Burn deals 5 damage to target black or green creature or planeswalker. " +
        "If that permanent would die this turn, exile it instead."

    spell {
        val permanent = target(
            "target black or green creature or planeswalker",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.CreatureOrPlaneswalker.withAnyColor(Color.BLACK, Color.GREEN)
                )
            )
        )
        effect = Effects.Composite(
            Effects.DealDamage(5, permanent),
            Effects.MarkExileOnDeath(permanent)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "82"
        artist = "Javier Charro"
        flavorText = "Witherbloom believed in life and death's intricate balance. Hexhaven was drawn to their unpredictable power."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2e958de-70de-4156-8f9b-b2c0c1ba704a.jpg?1789470825"
        inBooster = false
    }
}
