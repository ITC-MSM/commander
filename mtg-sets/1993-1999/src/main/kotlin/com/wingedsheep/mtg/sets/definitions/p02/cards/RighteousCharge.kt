package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Righteous Charge
 * {1}{W}{W}
 * Sorcery
 * Creatures you control get +2/+2 until end of turn.
 *
 * The untargeted mass pump: [Effects.ForEachInGroup] over the creatures you control, with the stat
 * change aimed at [EffectTarget.Self] — the current iteration entity — the same shape Rally the
 * Troops uses for its mass untap.
 */
val RighteousCharge = card("Righteous Charge") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Creatures you control get +2/+2 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(2, 2, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Jeffrey R. Busch"
        flavorText = "\"Bravery shines brightest in a pure soul.\"\n—Restela, Alaborn marshal"
        imageUri = "https://cards.scryfall.io/normal/front/9/f/9f7bd958-20c7-4394-8beb-06b32db90d32.jpg"
    }
}
