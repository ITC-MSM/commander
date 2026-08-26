package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Oketra's Monument
 * {3}
 * Legendary Artifact
 * White creature spells you cast cost {1} less to cast.
 * Whenever you cast a creature spell, create a 1/1 white Warrior creature token with vigilance.
 *
 * The two lines read different sets of spells on purpose: only *white* creature spells are cheaper,
 * but the token trigger fires on any creature spell you cast.
 */
val OketrasMonument = card("Oketra's Monument") {
    manaCost = "{3}"
    typeLine = "Legendary Artifact"
    oracleText = "White creature spells you cast cost {1} less to cast.\n" +
        "Whenever you cast a creature spell, create a 1/1 white Warrior creature token with vigilance."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Creature.withColor(Color.WHITE)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Warrior"),
            keywords = setOf(Keyword.VIGILANCE),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "233"
        artist = "Christine Choi"
        flavorText = "\"The worthy shall respect the worthy. In the afterlife, all will stand united.\"\n—Monument inscription"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/104503a6-bca5-48d7-88b1-424f98985d75.jpg?1783936451"
    }
}
