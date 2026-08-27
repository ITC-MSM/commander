package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Enthusiastic Mechanaut — Kamigawa: Neon Dynasty #218 (canonical printing)
 * {U}{R} · Artifact Creature — Goblin Artificer · 2/2
 *
 * Flying
 * Artifact spells you cast cost {1} less to cast.
 *
 * The Medallion shape with an artifact filter — [CostModification.ReduceGeneric] only shaves
 * generic mana, so a coloured artifact spell keeps its pips.
 */
val EnthusiasticMechanaut = card("Enthusiastic Mechanaut") {
    manaCost = "{U}{R}"
    colorIdentity = "UR"
    typeLine = "Artifact Creature — Goblin Artificer"
    power = 2
    toughness = 2
    oracleText = "Flying\nArtifact spells you cast cost {1} less to cast."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Artifact),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "218"
        artist = "Anna Steinbauer"
        flavorText = "\"Greetings, fellow inventors!\" the akki shouted, attempting a friendly " +
            "smile. To her dismay, the greeting was met only with shrieks and alarms being activated."
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac00521f-1b7d-478d-afe8-6761ea512d8d.jpg?1783923839"
    }
}
