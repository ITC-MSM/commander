package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ba Sing Se — Land — Rare
 *
 * This land enters tapped unless you control a basic land.
 * {T}: Add {G}.
 * {2}{G}, {T}: Earthbend 2. Activate only as a sorcery. (Target land you control
 * becomes a 0/0 creature with haste that's still a land. Put two +1/+1 counters on it.
 * When it dies or is exiled, return it to the battlefield tapped.)
 */
val BaSingSe = card("Ba Sing Se") {
    typeLine = "Land"
    colorIdentity = "G"
    oracleText = "This land enters tapped unless you control a basic land.\n" +
            "{T}: Add {G}.\n" +
            "{2}{G}, {T}: Earthbend 2. Activate only as a sorcery. (Target land you control becomes a 0/0 creature with haste that's still a land. Put two +1/+1 counters on it. When it dies or is exiled, return it to the battlefield tapped.)"

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.BasicLand)
        )
    )

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{G}"), Costs.Tap)
        timing = TimingRule.SorcerySpeed
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = Effects.Earthbend(2, land)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "266"
        artist = "Andreas Rocha"
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdf3b2be-d0cd-4a3c-a10e-82d32c12d3bd.jpg?1764121955"
    }
}
