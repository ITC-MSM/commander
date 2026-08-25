package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Kinjalli's Caller
 * {W}
 * Creature — Human Cleric
 * 0/3
 *
 * Dinosaur spells you cast cost {1} less to cast.
 */
val KinjallisCaller = card("Kinjalli's Caller") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "Dinosaur spells you cast cost {1} less to cast."
    power = 0
    toughness = 3

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype.DINOSAUR)),
            modification = CostModification.ReduceGeneric(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "18"
        artist = "Sara Winters"
        flavorText = "The people of the Sun Empire worship the sun in three aspects. Kinjalli is the Wakening Sun, who created humans from clay and baked them in the sun's warmth."
        imageUri = "https://cards.scryfall.io/normal/front/6/2/625211d4-c89c-4aee-a0b0-4bfabd3509ad.jpg"
    }
}
