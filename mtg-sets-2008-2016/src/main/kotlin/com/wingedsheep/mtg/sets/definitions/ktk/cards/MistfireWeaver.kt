package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mistfire Weaver
 * {3}{U}
 * Creature — Djinn Wizard
 * 3/1
 * Flying
 * Morph {2}{U}
 * When this creature is turned face up, target creature you control gains hexproof until end of turn.
 */
val MistfireWeaver = card("Mistfire Weaver") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Djinn Wizard"
    power = 3
    toughness = 1
    oracleText = "Flying\nMorph {2}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\nWhen this creature is turned face up, target creature you control gains hexproof until end of turn."

    keywords(Keyword.FLYING)

    morph = "{2}{U}"

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        val t = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.GrantKeyword(Keyword.HEXPROOF, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "46"
        artist = "Chris Rahn"
        imageUri = "https://cards.scryfall.io/normal/front/8/0/8003a31f-7662-4e9e-8165-1ceea04fdf20.jpg?1562789282"
    }
}
