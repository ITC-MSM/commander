package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * United Front
 * {X}{W}{W}
 * Sorcery
 * Create X 1/1 white Ally creature tokens, then put a +1/+1 counter on each creature you control.
 *
 * X is the {X} in the mana cost, surfaced at resolution as the spell's X value
 * ([DynamicAmount.XValue], cf. Weird Harvest / Riptide Replicator). The two clauses run in order
 * via [Effects.Composite]: the tokens are created first, then [Effects.ForEachInGroup] snapshots
 * "each creature you control" — which now includes the freshly created tokens — and drops a
 * +1/+1 counter on each ("then" sequences the counters after the tokens enter, CR 608.2c).
 */
val UnitedFront = card("United Front") {
    manaCost = "{X}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create X 1/1 white Ally creature tokens, then put a +1/+1 counter on each creature you control."

    spell {
        effect = Effects.Composite(
            Effects.CreateToken(
                count = DynamicAmount.XValue,
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Ally"),
            ),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                AddCountersEffect(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            ),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "39"
        artist = "Mengxuan Li"
        flavorText = "\"When this is finished, the Avatar will have defeated the Fire Lord, we " +
            "will have control of the Fire Nation capital, and this war will be over!\"\n—Hakoda"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d347caf8-0c12-401f-9e33-9978cb347f89.jpg?1764120154"
    }
}
