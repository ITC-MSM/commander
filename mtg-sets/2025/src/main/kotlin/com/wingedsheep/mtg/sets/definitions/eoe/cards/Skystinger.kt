package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skystinger
 * {2}{G}
 * Creature — Insect Warrior
 * 3/3
 * Reach
 * Whenever this creature blocks a creature with flying, this creature gets +5/+0 until end of turn.
 */
val Skystinger = card("Skystinger") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect Warrior"
    power = 3
    toughness = 3
    oracleText = "Reach\nWhenever this creature blocks a creature with flying, this creature gets +5/+0 until end of turn."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.blocks(attackerFilter = GameObjectFilter.Creature.withKeyword(Keyword.FLYING))
        effect = Effects.ModifyStats(5, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "207"
        artist = "Carlos Palma Cruchaga"
        flavorText = "Eumidian thermal wands protect their new home as well as thaw it."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/dfea9941-3675-4c0f-bc4d-981c28deed36.jpg?1752947402"
    }
}
