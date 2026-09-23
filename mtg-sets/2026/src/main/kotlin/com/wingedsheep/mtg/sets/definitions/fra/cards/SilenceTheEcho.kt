package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val SilenceTheEcho = card("Silence the Echo") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature or planeswalker or pay {3}.\n" +
        "Destroy target creature or planeswalker."

    additionalCost(
        Costs.additional.SacrificeOrPay(
            filter = GameObjectFilter.CreatureOrPlaneswalker,
            alternativeManaCost = "{3}",
        )
    )

    spell {
        target = Targets.CreatureOrPlaneswalker
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Pavel Kolomeyets"
        flavorText = "The Vraska who had never known pain could not comprehend the unyielding will of the Vraska who had known too much."
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1d274db-751b-4414-a38d-762198168e91.jpg?1789385640"
        inBooster = false
    }
}
