package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Metathran Transport
 * {1}{U}{U}
 * Creature — Metathran
 * 1/3
 * Flying
 * This creature can't be blocked by blue creatures.
 * {U}: Target creature becomes blue until end of turn.
 */
val MetathranTransport = card("Metathran Transport") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Metathran"
    power = 1
    toughness = 3
    oracleText = "Flying\n" +
        "This creature can't be blocked by blue creatures.\n" +
        "{U}: Target creature becomes blue until end of turn."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.withColor(Color.BLUE))
    }

    activatedAbility {
        cost = Costs.Mana("{U}")
        val t = target("target creature", Targets.Creature)
        effect = Effects.ChangeColor(target = t, colors = setOf(Color.BLUE))
        description = "{U}: Target creature becomes blue until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "62"
        artist = "Glen Angus"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4fa9048d-1599-44a5-b4b2-45382c5b238d.jpg?1562911137"
    }
}
