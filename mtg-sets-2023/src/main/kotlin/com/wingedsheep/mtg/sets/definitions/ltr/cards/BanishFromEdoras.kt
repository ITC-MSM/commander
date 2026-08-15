package com.wingedsheep.mtg.sets.definitions.ltr.cards

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
 * Banish from Edoras
 * {4}{W}
 * Sorcery
 *
 * This spell costs {2} less to cast if it targets a tapped creature.
 * Exile target creature.
 */
val BanishFromEdoras = card("Banish from Edoras") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "This spell costs {2} less to cast if it targets a tapped creature.\n" +
        "Exile target creature."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 2,
                    filter = GameObjectFilter.Creature.tapped()
                )
            )
        )
    }

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.Exile(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Veli Nyström"
        flavorText = "Wormtongue bared his teeth; and then with a hissing breath he spat before the king's feet."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4410076-e1fe-45f3-a0ca-a91ab0133ff4.jpg?1686967642"
    }
}
