package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Aerie Bowmasters
 * {2}{G}{G}
 * Creature — Dog Archer
 * 3/4
 * Reach
 * Megamorph {5}{G}
 *
 * Megamorph is modeled as the Morph keyword with a face-up effect that puts a
 * +1/+1 counter on the creature when it is turned face up (CR 702.37 / 702.36).
 * The card may be cast face down as a 2/2 for {3}; turning it face up for the
 * megamorph cost executes the face-up effect.
 */
val AerieBowmasters = card("Aerie Bowmasters") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dog Archer"
    power = 3
    toughness = 4
    oracleText = "Reach (This creature can block creatures with flying.)\n" +
        "Megamorph {5}{G} (You may cast this card face down as a 2/2 creature for {3}. " +
        "Turn it face up any time for its megamorph cost and put a +1/+1 counter on it.)"

    keywords(Keyword.REACH)

    // Megamorph {5}{G}: turn face up for the cost, then put a +1/+1 counter on it.
    morph = "{5}{G}"
    morphFaceUpEffect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "170"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6db26b5-d28c-4524-9347-eec412d584bc.jpg?1562791100"
    }
}
