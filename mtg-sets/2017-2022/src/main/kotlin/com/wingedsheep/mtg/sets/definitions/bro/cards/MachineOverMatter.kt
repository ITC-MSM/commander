package com.wingedsheep.mtg.sets.definitions.bro.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Machine Over Matter
 * {1}{U}
 * Instant
 * This spell costs {1} less to cast if you control an artifact creature.
 * Return target nonland permanent to its owner's hand.
 */
val MachineOverMatter = card("Machine Over Matter") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast if you control an artifact creature.\n" +
        "Return target nonland permanent to its owner's hand."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.OnlyIf(Conditions.YouControl(GameObjectFilter.ArtifactCreature))
        )
    }

    spell {
        val t = target("target", Targets.NonlandPermanent)
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Tuan Duong Chu"
        flavorText = "The Warlord decreed that only someone who could move the statue would be strong enough to marry the princess. So Urza moved it."
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d08eadf5-a86d-4e8d-b65d-79b4f88477b9.jpg?1783920109"
    }
}
