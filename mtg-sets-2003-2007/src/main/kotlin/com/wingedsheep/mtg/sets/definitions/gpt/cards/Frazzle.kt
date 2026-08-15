package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Frazzle
 * {3}{U}
 * Instant
 * Counter target nonblue spell.
 */
val Frazzle = card("Frazzle") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target nonblue spell."

    spell {
        target = TargetSpell(filter = TargetFilter.SpellOnStack.notColor(Color.BLUE))
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "25"
        artist = "Pete Venters"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/68b7f705-4d64-4551-8d76-826d91324e9e.jpg?1593271993"
    }
}
