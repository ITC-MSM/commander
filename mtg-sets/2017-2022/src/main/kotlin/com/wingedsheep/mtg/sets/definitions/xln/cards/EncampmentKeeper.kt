package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Encampment Keeper
 * {W}
 * Creature — Dog
 * 1/1
 *
 * First strike
 * {7}{W}, {T}, Sacrifice this creature: Creatures you control get +2/+2 until end of turn.
 */
val EncampmentKeeper = card("Encampment Keeper") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog"
    oracleText = "First strike\n" +
        "{7}{W}, {T}, Sacrifice this creature: Creatures you control get +2/+2 until end of turn."
    power = 1
    toughness = 1

    keywords(Keyword.FIRST_STRIKE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}{W}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.ModifyStats(2, 2, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Craig J Spearing"
        flavorText = "Paladins of the Sanctum Seeker order are an adventurous lot, venturing into the wilds with monstrous mastiffs at their side."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6fe64569-c1b6-4bd4-a742-6ee46ea5181c.jpg"
    }
}
