package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flamekin Spitfire
 * {1}{R}
 * Creature — Elemental Shaman
 * 1/1
 * {3}{R}: This creature deals 1 damage to any target.
 */
val FlamekinSpitfire = card("Flamekin Spitfire") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental Shaman"
    power = 1
    toughness = 1
    oracleText = "{3}{R}: This creature deals 1 damage to any target."

    activatedAbility {
        cost = Costs.Mana("{3}{R}")
        val recipient = target("any target", Targets.Any)
        effect = Effects.DealDamage(1, recipient)
        description = "{3}{R}: This creature deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "168"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "Some flamekin warriors explore the art of coherence, an ancient discipline that harnesses the chaos of fire and focuses it with pinpoint precision."
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a7878e5-fcb0-4de5-8dda-fcdb4b138ec1.jpg?1783942876"
    }
}
