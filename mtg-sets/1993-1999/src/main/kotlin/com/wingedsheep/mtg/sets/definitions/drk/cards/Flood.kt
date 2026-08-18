package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Flood
 * {U}
 * Enchantment
 * {U}{U}: Tap target creature without flying.
 */
val Flood = card("Flood") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "{U}{U}: Tap target creature without flying."

    activatedAbility {
        cost = Costs.Mana("{U}{U}")
        val creature = target(
            "target creature without flying",
            TargetCreature(filter = TargetFilter.Creature.withoutKeyword(Keyword.FLYING))
        )
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "27"
        artist = "Dennis Detwiller"
        flavorText = "\"A dash of cool water does wonders to clear a cluttered battlefield.\" —Vibekke Ragnild, *Witches and War*"
        imageUri = "https://cards.scryfall.io/normal/front/f/a/fabc3267-b59b-4f36-8873-5b4b072711ca.jpg?1783947944"
    }
}
