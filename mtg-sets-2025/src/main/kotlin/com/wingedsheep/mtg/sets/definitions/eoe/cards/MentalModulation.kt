package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.conditions.IsYourTurn
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Mental Modulation
 * {1}{U}
 * Instant
 * This spell costs {1} less to cast during your turn.
 * Tap target artifact or creature.
 * Draw a card.
 */
val MentalModulation = card("Mental Modulation") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {1} less to cast during your turn.\n" +
        "Tap target artifact or creature.\n" +
        "Draw a card."

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(1),
            gating = CostGating.OnlyIf(IsYourTurn),
        )
    }

    spell {
        val target = target(
            "target artifact or creature",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact or GameObjectFilter.Creature)),
        )
        effect = Effects.Tap(target)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Andreia Ugrai"
        flavorText = "Illvoi nervous systems are decentralized. Even as one part runs calculations, another might contemplate the depths of Uthros."
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f2d12fc-38a0-42e6-9caa-7c18bfcf0011.jpg?1752946821"
    }
}
