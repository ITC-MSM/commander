package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Poultice Sliver
 * {2}{W}
 * Creature — Sliver
 * 2/2
 * All Slivers have "{2}, {T}: Regenerate target Sliver."
 *
 * "Target **Sliver**" is the bare tribal noun again — any Sliver permanent, matching the group the
 * ability is granted to.
 */
val PoulticeSliver = card("Poultice Sliver") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "All Slivers have \"{2}, {T}: Regenerate target Sliver.\" (The next time that Sliver would be destroyed this turn, instead tap it, remove it from combat, and heal all damage on it.)"

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap),
                effect = RegenerateEffect(EffectTarget.BoundVariable("target")),
                targetRequirements = listOf(
                    TargetObject(
                        filter = TargetFilter(GameObjectFilter.Permanent.withSubtype(Subtype.SLIVER)),
                        id = "target"
                    )
                )
            ),
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.SLIVER))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Randy Gallegos"
        flavorText = "\"Its broad claw suggests a chitinous shield, but in fact it conceals glands that secrete a remarkably swift healing agent.\"\n—Rukarumel, field journal"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3441ae0-4c20-487b-99b8-d6934dfb66ff.jpg"
    }
}
