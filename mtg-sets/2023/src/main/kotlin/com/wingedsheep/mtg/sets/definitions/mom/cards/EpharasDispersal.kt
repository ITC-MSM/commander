package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Ephara's Dispersal
 * {2}{U}
 * Instant
 * This spell costs {2} less to cast if it targets an attacking creature.
 * Return target creature to its owner's hand. Surveil 2.
 *
 * The reduction is [CostReductionSource.FixedIfAnyTargetMatches] — a *generic* reduction gated on
 * the spell's own chosen target, evaluated as the spell is cast. It touches only the total cost, so
 * this card's mana value stays 3 (the printed ruling).
 */
val EpharasDispersal = card("Ephara's Dispersal") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "This spell costs {2} less to cast if it targets an attacking creature.\n" +
        "Return target creature to its owner's hand. Surveil 2. (Look at the top two cards of " +
        "your library, then put any number of them into your graveyard and the rest on top of " +
        "your library in any order.)"

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.FixedIfAnyTargetMatches(
                    amount = 2,
                    filter = GameObjectFilter.Creature.attacking()
                )
            )
        )
    }

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ReturnToHand(creature) then Patterns.Library.surveil(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Awanqi (Angela Wang)"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0bcc71f-72ec-4a76-9393-ed3ea61eeeb6.jpg?1783917039"
        ruling(
            "2023-04-14",
            "The cost reduction ability of Ephara's Dispersal doesn't affect its mana cost or mana " +
                "value. It affects only the total cost you pay. Specifically, its mana value is always 3."
        )
        ruling(
            "2024-01-12",
            "You perform the actions stated on a card in sequence. For some spells and abilities, " +
                "you'll surveil last. For others, you'll surveil and then perform other actions."
        )
    }
}
