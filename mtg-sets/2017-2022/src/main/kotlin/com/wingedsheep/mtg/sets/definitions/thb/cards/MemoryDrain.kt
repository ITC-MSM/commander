package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Memory Drain
 * {2}{U}{U}
 * Instant
 *
 * Counter target spell. Scry 2.
 *
 * Two sentences in one effect, so the scry is not gated on the counter: [Effects.Composite] runs
 * [Effects.CounterSpell] then [Effects.Scry], and a spell that has already left the stack does not
 * stop the second half. The same shape as Theros' own Dissolve, one scry deeper.
 */
val MemoryDrain = card("Memory Drain") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. Scry 2."

    spell {
        target = Targets.Spell
        effect = Effects.Composite(
            Effects.CounterSpell(),
            Effects.Scry(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "PINDURSKI"
        flavorText = "Alythos sat and stared blankly out over the glassy oceans of Nerono, trying in vain to " +
            "remember what he knew he had forgotten."
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aadc1809-d6bb-455c-b6ce-dd11521808b6.jpg"
    }
}
