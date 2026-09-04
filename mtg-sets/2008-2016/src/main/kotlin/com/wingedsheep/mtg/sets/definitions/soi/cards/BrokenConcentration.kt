package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity

/**
 * Broken Concentration (Shadows over Innistrad #50)
 * {1}{U}{U}
 * Instant
 *
 * Counter target spell.
 * Madness {3}{U}
 */
val BrokenConcentration = card("Broken Concentration") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell.\n" +
        "Madness {3}{U} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    spell {
        target("target", Targets.Spell)
        effect = Effects.CounterSpell()
    }

    madness("{3}{U}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Clint Cearley"
        flavorText = "Some minds bend under pressure. Others break."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/252eef1f-0a62-420d-aad8-e3d7f1e07c1b.jpg?1783937804"
    }
}
