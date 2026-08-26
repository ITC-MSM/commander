package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flamecast Wheel
 * {1}
 * Artifact
 *
 * {5}, {T}, Sacrifice this artifact: It deals 3 damage to target creature.
 *
 * The wheel itself is the damage source ("It deals"), which is what [Effects.DealDamage] uses
 * when no explicit `damageSource` is given.
 */
val FlamecastWheel = card("Flamecast Wheel") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{5}, {T}, Sacrifice this artifact: It deals 3 damage to target creature."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{5}"),
            Costs.Tap,
            Costs.SacrificeSelf,
        )
        val t = target("target", Targets.Creature)
        effect = Effects.DealDamage(3, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Jasper Sandner"
        flavorText = "Beware the gifts of an ill-tempered forge god."
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5cfa31c9-5a11-4366-9063-056a659f3d0d.jpg"
    }
}
