package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * North Pole Patrol
 * {2}{U}
 * Creature — Human Soldier Ally
 * 2/3
 * {T}: Untap another target permanent you control.
 * Waterbend {3}, {T}: Tap target creature an opponent controls. (While paying a waterbend cost,
 *   you can tap your artifacts and creatures to help. Each one pays for {1}.)
 *
 * Both abilities reuse existing primitives: the untap is a plain targeted [Effects.Untap]; the
 * waterbend ability is a `Costs.Composite(Mana, Tap)` carrying `hasWaterbend = true`, so the engine
 * extracts the {3} generic and lets the player tap their artifacts/creatures to help pay it while the
 * Patrol's own tap is consumed by the activation cost.
 */
val NorthPolePatrol = card("North Pole Patrol") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Soldier Ally"
    power = 2
    toughness = 3
    oracleText = "{T}: Untap another target permanent you control.\n" +
        "Waterbend {3}, {T}: Tap target creature an opponent controls. (While paying a waterbend " +
        "cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)"

    activatedAbility {
        cost = Costs.Tap
        val permanent = target(
            "another target permanent you control",
            TargetPermanent(filter = TargetFilter.Permanent.youControl().other()),
        )
        effect = Effects.Untap(permanent)
        description = "{T}: Untap another target permanent you control."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        hasWaterbend = true
        val theirCreature = target(
            "target creature an opponent controls",
            Targets.CreatureOpponentControls,
        )
        effect = Effects.Tap(theirCreature)
        description = "Waterbend {3}, {T}: Tap target creature an opponent controls."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "65"
        artist = "Rose Benjamin"
        flavorText = "Waterbenders patrol the coast each day for good fishing spots and bad weather omens."
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2c0c138-5b61-4949-8431-4a6d458ead6a.jpg?1764120386"
    }
}
