package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Extinguish
 * {1}{U}
 * Instant
 * Counter target sorcery spell.
 *
 * Portal Second Age is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here.
 *
 * The narrowest counterspell in the Portal line — a sorcery is the only spell type a Portal
 * opponent can have on the stack while this instant is castable, which is the whole design.
 */
val Extinguish = card("Extinguish") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target sorcery spell."

    spell {
        target = TargetSpell(filter = TargetFilter.SorcerySpellOnStack)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/641f4e66-b46b-4da3-a053-f3763400d4f5.jpg?1783946486"
    }
}
