package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Firewake Sliver
 * {1}{R}{G}
 * Creature — Sliver
 * 1/1
 * All Sliver creatures have haste.
 * All Slivers have "{1}, Sacrifice this permanent: Target Sliver creature gets +2/+2 until end of turn."
 *
 * The haste grant reaches Sliver *creatures*; the granted ability reaches every Sliver *permanent*.
 */
val FirewakeSliver = card("Firewake Sliver") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Sliver"
    power = 1
    toughness = 1
    oracleText = "All Sliver creatures have haste.\n" +
        "All Slivers have \"{1}, Sacrifice this permanent: Target Sliver creature gets +2/+2 until end of turn.\""

    staticAbility {
        ability = GrantKeyword(
            Keyword.HASTE,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Sliver"))
        )
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf),
                effect = Effects.ModifyStats(2, 2, EffectTarget.BoundVariable("target")),
                targetRequirements = listOf(
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Creature.withSubtype("Sliver")),
                        id = "target"
                    )
                )
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype("Sliver"))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Anthony S. Waters"
        flavorText = "\"They are here, and they are hungry. And what they do not eat, they burn. Yavimaya is lost. We must leave now for Skyshroud.\"\n—Edahlis, greenseeker"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d3f5a0d-029e-44fd-b3e6-c70176b5b4ac.jpg"
    }
}
