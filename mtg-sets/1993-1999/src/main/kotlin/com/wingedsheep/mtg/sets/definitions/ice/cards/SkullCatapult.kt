package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Skull Catapult
 * {4}
 * Artifact
 *
 * {1}, {T}, Sacrifice a creature: This artifact deals 2 damage to any target.
 *
 * The sacrifice is a cost atom rather than an effect, so the whole card is one composite cost plus
 * a plain [Effects.DealDamage] — the damage source defaults to the artifact itself, so it is not
 * restated.
 */
val SkullCatapult = card("Skull Catapult") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}, Sacrifice a creature: This artifact deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "336"
        artist = "Bryon Wackwitz"
        flavorText = "\"Let any who doubt the evil of using the ancient devices look at this infernal machine. What manner of fiend would design such a sadistic device?\"\n—Sorine Relicbane, Soldevi Heretic"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb92a3e6-dc30-4a08-baba-e125290cadc5.jpg"
    }
}
