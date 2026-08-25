package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Slash of Talons
 * {W}
 * Instant
 *
 * Slash of Talons deals 2 damage to target attacking or blocking creature.
 */
val SlashOfTalons = card("Slash of Talons") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Slash of Talons deals 2 damage to target attacking or blocking creature."

    spell {
        val victim = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.attackingOrBlocking()))
        )
        effect = Effects.DealDamage(2, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Magali Villeneuve"
        flavorText = "\"The amber sun smokes with fury, gazing on foes that gather like ants invading our home. We are ready! Blade and claw strike as one.\"\n—Huatli"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a97d474e-83b6-4969-81b4-51f5315057d7.jpg"
    }
}
