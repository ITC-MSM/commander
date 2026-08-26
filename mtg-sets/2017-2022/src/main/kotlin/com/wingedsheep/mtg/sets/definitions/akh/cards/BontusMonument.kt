package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bontu's Monument
 * {3}
 * Legendary Artifact
 * Black creature spells you cast cost {1} less to cast.
 * Whenever you cast a creature spell, each opponent loses 1 life and you gain 1 life.
 *
 * The two lines read different sets of spells on purpose: only *black* creature spells are cheaper,
 * but the drain trigger fires on any creature spell you cast.
 */
val BontusMonument = card("Bontu's Monument") {
    manaCost = "{3}"
    typeLine = "Legendary Artifact"
    oracleText = "Black creature spells you cast cost {1} less to cast.\n" +
        "Whenever you cast a creature spell, each opponent loses 1 life and you gain 1 life."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Creature.withColor(Color.BLACK)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Jonas De Ro"
        flavorText = "\"The worthy shall strive for greatness. Supremacy in life leads to supremacy in the afterlife.\"\n—Monument inscription"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9b00377-9acc-47c4-ae2f-b396d7050a15.jpg?1783936454"
    }
}
