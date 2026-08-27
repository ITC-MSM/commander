package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Necrotic Sliver
 * {1}{W}{B}
 * Creature — Sliver
 * 2/2
 * All Slivers have "{3}, Sacrifice this permanent: Destroy target permanent."
 *
 * A granted ability carries its own target requirement, so the destroy targets a bound variable
 * on the *granted* ability rather than on Necrotic Sliver's own script.
 */
val NecroticSliver = card("Necrotic Sliver") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Slivers have \"{3}, Sacrifice this permanent: Destroy target permanent.\""

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                cost = Costs.Composite(Costs.Mana("{3}"), Costs.SacrificeSelf),
                effect = Effects.Move(
                    EffectTarget.BoundVariable("target"),
                    Zone.GRAVEYARD,
                    byDestruction = true
                ),
                targetRequirements = listOf(
                    TargetObject(filter = TargetFilter.Permanent, id = "target")
                )
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "Dave Allsop"
        flavorText = "Though Volrath is long dead, the slivers have become everything he wanted them to be: mindless instruments of destruction and despair."
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44e18843-06b4-480c-9291-c502542f72b1.jpg"
    }
}
