package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Torch Fiend
 * {1}{R}
 * Creature — Devil
 * 2/1
 *
 * {R}, Sacrifice this creature: Destroy target artifact.
 */
val TorchFiend = card("Torch Fiend") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Devil"
    oracleText = "{R}, Sacrifice this creature: Destroy target artifact."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.SacrificeSelf)
        target = Targets.Artifact
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
        description = "{R}, Sacrifice this creature: Destroy target artifact."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "106"
        artist = "Winona Nelson"
        flavorText = "Devils redecorate every room with fire."
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d596feee-6ccc-4648-884b-ed2eeb1cffc0.jpg?1783940810"
    }
}
