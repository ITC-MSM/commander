package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.GrantAdditionalTypesToGroup
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ragost, Deft Gastronaut
 * {R}{W}
 * Legendary Creature — Lobster Citizen
 * 2/2
 *
 * Artifacts you control are Foods in addition to their other types and
 * have "{2}, {T}, Sacrifice this artifact: You gain 3 life."
 * {1}, {T}, Sacrifice a Food: Ragost deals 3 damage to each opponent.
 * At the beginning of each end step, if you gained life this turn, untap Ragost.
 */
val RagostDeftGastronaut = card("Ragost, Deft Gastronaut") {
    manaCost = "{R}{W}"
    colorIdentity = "RW"
    typeLine = "Legendary Creature — Lobster Citizen"
    power = 2
    toughness = 2
    oracleText = "Artifacts you control are Foods in addition to their other types and have " +
        "\"{2}, {T}, Sacrifice this artifact: You gain 3 life.\"\n" +
        "{1}, {T}, Sacrifice a Food: Ragost deals 3 damage to each opponent.\n" +
        "At the beginning of each end step, if you gained life this turn, untap Ragost."

    // Artifacts you control are Foods in addition to their other types
    staticAbility {
        ability = GrantAdditionalTypesToGroup(
            filter = GroupFilter(GameObjectFilter.Artifact.youControl()),
            addSubtypes = listOf("Food")
        )
    }

    // ...and have "{2}, {T}, Sacrifice this artifact: You gain 3 life."
    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf),
                effect = Effects.GainLife(3)
            ),
            filter = GroupFilter(GameObjectFilter.Artifact.youControl())
        )
    }

    // {1}, {T}, Sacrifice a Food: Ragost deals 3 damage to each opponent.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Any.withSubtype("Food"))
        )
        effect = Effects.DealDamage(3, EffectTarget.PlayerRef(Player.EachOpponent))
    }

    // At the beginning of each end step, if you gained life this turn, untap Ragost.
    triggeredAbility {
        trigger = Triggers.EachEndStep
        triggerCondition = Conditions.YouGainedLifeThisTurn
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "224"
        artist = "Zack Stella"
        imageUri = "https://cards.scryfall.io/normal/front/0/1/011374c3-f69d-4573-8c32-5bd0fe083d6a.jpg?1752947475"

        ruling("2025-07-25", "The artifacts retain any types, subtypes, supertypes, and abilities they have.")
        ruling("2025-07-25", "If Ragost somehow becomes an artifact, Ragost will also be a Food.")
        ruling("2025-07-25", "Ragost's last ability will check as the end step starts to see if you gained life this turn. If you haven't, the ability won't trigger at all. You won't be able to gain life during your end step in time to have the ability trigger.")
        ruling("2025-07-25", "If an effect refers to a Food, it means any Food artifact, not just a Food token. For example, when you activate Ragost's second ability, you can sacrifice any Food you control to pay its cost.")
        ruling("2025-07-25", "You can't sacrifice a Food to pay multiple costs. For example, you can't sacrifice a single Food to pay the cost of its own ability and also to pay the cost of Ragost's second ability.")
    }
}
