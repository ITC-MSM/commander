package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Wizard's Retort
 * {1}{U}{U}
 * Instant
 * This spell costs {1} less to cast if you control a Wizard.
 * Counter target spell.
 */
val WizardsRetort = card("Wizard's Retort") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast if you control a Wizard.\nCounter target spell."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfControlFilter(
                    amount = 1,
                    filter = GameObjectFilter.Any.withSubtype("Wizard"),
                ),
            ),
        )
    }

    spell {
        target = Targets.Spell
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "75"
        artist = "Grzegorz Rutkowski"
        flavorText = "\"The second mage learned to dissipate blasts of lightning. Threat and response: thus did the study of magic progress.\" —Naban, dean of iteration"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/bae30b7d-9306-46ef-adea-c4057f59c9c1.jpg?1562741944"
    }
}
