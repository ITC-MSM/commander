package com.wingedsheep.mtg.sets.definitions.ulg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Miscalculation
 * {1}{U}
 * Instant
 * Counter target spell unless its controller pays {2}.
 * Cycling {2}
 *
 * The Urza's Legacy original of the tax-counter-plus-cycling shape Onslaught later reprinted as
 * Complicate — [Effects.CounterUnlessPays] over [Targets.Spell], with cycling as a plain
 * [KeywordAbility.cycling]. Unlike Complicate there is no cycling trigger: cycling Miscalculation
 * just draws.
 */
val Miscalculation = card("Miscalculation") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell unless its controller pays {2}.\nCycling {2}"

    spell {
        target = Targets.Spell
        effect = Effects.CounterUnlessPays("{2}")
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Jeff Laubenstein"
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b4956a2-9a39-4152-9c98-70e4b2acfa26.jpg?1783946245"

        ruling(
            "2008-10-01",
            "Cycling is an activated ability. Effects that interact with activated abilities " +
                "(such as Stifle or Rings of Brighthearth) will interact with cycling. Effects " +
                "that interact with spells (such as Remove Soul or Faerie Tauntings) will not."
        )
    }
}
