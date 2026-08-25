package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Flash Counter
 * {1}{U}
 * Instant
 *
 * Counter target instant spell.
 */
val FlashCounter = card("Flash Counter") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target instant spell."

    spell {
        target("target instant spell", TargetSpell(filter = TargetFilter.InstantSpellOnStack))
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Harold McNeill"
        flavorText = "\"She grinned at me—a wicked grin. 'I hope you weren't relying too heavily on that, my " +
            "dear.'\"\n" +
            "—Medryn Silverwand, *Diary*"
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c3cd450-f1cd-416b-9271-37d95815c089.jpg?1783948075"
    }
}
