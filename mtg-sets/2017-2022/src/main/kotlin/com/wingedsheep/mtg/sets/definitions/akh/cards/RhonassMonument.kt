package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Rhonas's Monument
 * {3}
 * Legendary Artifact
 * Green creature spells you cast cost {1} less to cast.
 * Whenever you cast a creature spell, target creature you control gets +2/+2 and gains trample until end of turn.
 *
 * The two lines read different sets of spells on purpose: only *green* creature spells are cheaper,
 * but the pump trigger fires on any creature spell you cast.
 */
val RhonassMonument = card("Rhonas's Monument") {
    manaCost = "{3}"
    typeLine = "Legendary Artifact"
    oracleText = "Green creature spells you cast cost {1} less to cast.\n" +
        "Whenever you cast a creature spell, target creature you control gets +2/+2 and gains trample until end of turn."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Creature.withColor(Color.GREEN)),
            modification = CostModification.ReduceGeneric(1),
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastCreature
        val t = target("target", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ModifyStats(2, 2, t),
            Effects.GrantKeyword(Keyword.TRAMPLE, t),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "236"
        artist = "Cliff Childs"
        flavorText = "\"The worthy shall hone a strong body to endure the boundless energies of the afterlife.\"\n—Monument inscription"
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53d2260a-e001-4d03-a108-759591e4d233.jpg?1783936447"
    }
}
