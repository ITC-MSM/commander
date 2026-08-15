package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Grow Extra Arms
 * {1}{G}
 * Instant
 * This spell costs {1} less to cast if it targets a Spider.
 * Target creature gets +4/+4 until end of turn.
 */
val GrowExtraArms = card("Grow Extra Arms") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast if it targets a Spider.\nTarget creature gets +4/+4 until end of turn."
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 1,
                    filter = GameObjectFilter.Creature.withSubtype(Subtype.SPIDER)
                )
            )
        )
    }
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(4, 4, t)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Kevin Sidharta"
        flavorText = "\"I created that potion to rob me of my spider-powers. I wanted it to change me forever. But not like this! NOT LIKE THIS!\""
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63fab399-00db-4398-922e-c3ca3356731a.jpg?1757377428"
    }
}
