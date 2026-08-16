package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Bone Splinters
 * {B}
 * Sorcery
 * As an additional cost to cast this spell, sacrifice a creature.
 * Destroy target creature.
 */
val BoneSplinters = card("Bone Splinters") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\nDestroy target creature."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Cole Eastburn"
        flavorText = "Witches of the Split-Eye Coven speak of a future when Grixis will overflow with life energy. For now, they must harvest vis from the living to fuel their dark magics."
        imageUri = "https://cards.scryfall.io/normal/front/d/4/d4a4b3a3-b7ae-4210-8037-098fdf5808d0.jpg?1783942568"
    }
}
