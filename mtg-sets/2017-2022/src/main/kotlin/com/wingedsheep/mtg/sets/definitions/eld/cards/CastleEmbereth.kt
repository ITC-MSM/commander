package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Castle Embereth
 *
 * Land
 * This land enters tapped unless you control a Mountain.
 * {T}: Add {R}.
 * {1}{R}{R}, {T}: Creatures you control get +1/+0 until end of turn.
 *
 * Three stock land pieces, nothing bespoke: [EntersTapped] carries the entry clause with an
 * `unlessCondition` of [Exists] over Mountains you control (Arena of Glory's shape), the type line
 * is bare `Land` so the {R} tap has to be written out as a real mana ability, and the pump is one
 * [Patterns.Group.modifyStatsForAll] pass over `creaturesYouControl` — one group named once, which
 * is why it is a single `ForEachInGroup` rather than a composition.
 */
val CastleEmbereth = card("Castle Embereth") {
    manaCost = ""
    colorIdentity = "R"
    typeLine = "Land"
    oracleText = "This land enters tapped unless you control a Mountain.\n" +
        "{T}: Add {R}.\n" +
        "{1}{R}{R}, {T}: Creatures you control get +1/+0 until end of turn."

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(
                Player.You,
                Zone.BATTLEFIELD,
                GameObjectFilter.Land.withSubtype("Mountain"),
            )
        )
    )

    // {T}: Add {R}.
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    // {1}{R}{R}, {T}: Creatures you control get +1/+0 until end of turn.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}{R}"), Costs.Tap)
        effect = Patterns.Group.modifyStatsForAll(1, 0, Filters.Group.creaturesYouControl)
        description = "{1}{R}{R}, {T}: Creatures you control get +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "239"
        artist = "Jaime Jones"
        flavorText = "Without Embereth's courage, the realm would falter and fall."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bb8512e-6913-4be6-8828-24cfcbec042e.jpg?1783932580"
    }
}
