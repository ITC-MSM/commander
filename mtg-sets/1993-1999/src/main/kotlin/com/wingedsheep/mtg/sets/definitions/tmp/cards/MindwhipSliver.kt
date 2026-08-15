package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Mindwhip Sliver
 * {2}{B}
 * Creature — Sliver
 * 2/2
 * All Slivers have "{2}, Sacrifice this permanent: Target player discards a card at
 * random. Activate only as a sorcery."
 */
val MindwhipSliver = card("Mindwhip Sliver") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Slivers have \"{2}, Sacrifice this permanent: Target player discards a card at random. " +
        "Activate only as a sorcery.\""

    val sliverFilter = GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf),
                timing = TimingRule.SorcerySpeed,
                effect = Patterns.Hand.discardRandom(1, EffectTarget.ContextTarget(0)),
                targetRequirement = TargetPlayer()
            ),
            filter = sliverFilter
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "145"
        artist = "Jeff Miracola"
        flavorText = "\"They share more than their thoughts. We must shatter their link quickly!\"\n—Hanna, to Orim"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fa966fbb-140d-4057-a4fc-998ebe07c307.jpg?1562057377"
    }
}
