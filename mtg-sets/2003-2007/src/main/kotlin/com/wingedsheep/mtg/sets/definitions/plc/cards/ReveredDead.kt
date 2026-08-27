package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Revered Dead
 * {1}{W}
 * Creature — Spirit Soldier
 * 1/1
 * {W}: Regenerate this creature.
 */
val ReveredDead = card("Revered Dead") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit Soldier"
    power = 1
    toughness = 1
    oracleText = "{W}: Regenerate this creature."

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = RegenerateEffect(EffectTarget.Self)
        description = "{W}: Regenerate this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Ron Spears"
        flavorText = "\"The mists coalesced into silent warriors. We charged them and broke through their lines, only to see them swirl and re-form behind us.\"\n—Golas Mahr, black knight"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9a78d5c-27f8-4061-be89-0246fb69e752.jpg"
    }
}
