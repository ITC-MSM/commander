package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Clockwork Drawbridge
 * {W}
 * Artifact Creature — Wall
 * 0/3
 * Defender
 * {2}{W}, {T}: Tap target creature.
 */
val ClockworkDrawbridge = card("Clockwork Drawbridge") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Wall"
    oracleText = "Defender\n{2}{W}, {T}: Tap target creature."
    power = 0
    toughness = 3

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Nadia Hurianova"
        flavorText = "Argivia's clockwork fortifications are the stuff of legend, reminding all on Dominaria that ingenuity has saved them before and can do so again."
        imageUri = "https://cards.scryfall.io/normal/front/0/3/037cb79c-163d-4b36-bd93-954eca8fe26e.jpg?1783921369"
    }
}
