package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Gaze of Justice
 * {W}
 * Sorcery
 * As an additional cost to cast this spell, tap three untapped white creatures you control.
 * Exile target creature.
 * Flashback {5}{W} (You may cast this card from your graveyard for its flashback cost and any
 * additional costs. Then exile it.)
 */
val GazeOfJustice = card("Gaze of Justice") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, tap three untapped white creatures you control.\n" +
        "Exile target creature.\n" +
        "Flashback {5}{W} (You may cast this card from your graveyard for its flashback cost and any additional costs. Then exile it.)"

    additionalCost(
        Costs.additional.TapPermanents(
            count = 3,
            filter = GameObjectFilter.Creature.withColor(Color.WHITE)
        )
    )

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Exile(t)
    }

    keywordAbility(KeywordAbility.flashback("{5}{W}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24d565ec-541d-429e-ab45-58db16c2f41d.jpg"
    }
}
