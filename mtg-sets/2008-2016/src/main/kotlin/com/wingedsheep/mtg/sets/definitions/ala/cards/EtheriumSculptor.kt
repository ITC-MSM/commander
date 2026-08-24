package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Etherium Sculptor
 * {1}{U}
 * Artifact Creature — Vedalken Artificer
 * 1 / 2
 * Artifact spells you cast cost {1} less to cast.
 *
 * A plain generic-cost reduction on the controller's own artifact spells
 * ([SpellCostTarget.YouCast] over [GameObjectFilter.Artifact] + [CostModification.ReduceGeneric]).
 * Generic-only, so it never shaves a coloured pip, and — like every cost static — it functions only
 * while the Sculptor is on the battlefield, so it never discounts itself.
 */
val EtheriumSculptor = card("Etherium Sculptor") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Vedalken Artificer"
    power = 1
    toughness = 2
    oracleText = "Artifact spells you cast cost {1} less to cast."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Artifact),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "Steven Belledin"
        flavorText = "The greatest masters of the craft abandon tools altogether, shaping metal with hand and mind alone."
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0d050f2d-bd65-4ab9-9ea6-9deba91b2792.jpg"
    }
}
