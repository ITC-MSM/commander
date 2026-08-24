package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Molderhulk
 * {7}{B}{G}
 * Creature — Fungus Zombie
 * 6/6
 * Undergrowth — This spell costs {1} less to cast for each creature card in your graveyard.
 * When this creature enters, return target land card from your graveyard to the battlefield.
 */
val Molderhulk = card("Molderhulk") {
    manaCost = "{7}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Fungus Zombie"
    oracleText = "Undergrowth — This spell costs {1} less to cast for each creature card in your graveyard.\n" +
        "When this creature enters, return target land card from your graveyard to the battlefield."
    power = 6
    toughness = 6

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.CardsInGraveyardMatchingFilter(GameObjectFilter.Creature)
            )
        )
    }
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val land = target(
            "target",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.Land.ownedByYou(), zone = Zone.GRAVEYARD)
            )
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(land)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba88e031-b194-4621-9e97-2f33ee46f6d0.jpg?1783934125"
    }
}
