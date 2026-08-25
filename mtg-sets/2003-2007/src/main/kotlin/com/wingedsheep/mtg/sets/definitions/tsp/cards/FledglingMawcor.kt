package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fledgling Mawcor
 * {3}{U}
 * Creature — Beast
 * 2 / 2
 * Flying
 * {T}: This creature deals 1 damage to any target.
 * Morph {U}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any
 * time for its morph cost.)
 *
 * Morph is the bare `morph` string property — `CardBuilder.build()` turns it into the
 * `KeywordAbility.Morph` whose turn-up price is a mana cost atom.
 */
val FledglingMawcor = card("Fledgling Mawcor") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "{T}: This creature deals 1 damage to any target.\n" +
        "Morph {U}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)"

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(1, t)
    }

    morph = "{U}{U}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c464923e-ae6e-4c1d-9315-0ddb86c07b40.jpg"
    }
}
