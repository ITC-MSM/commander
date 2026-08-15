package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sandskitter Outrider — Tarkir: Dragonstorm #89
 * {3}{B} · Creature — Goblin Soldier · 2/1
 *
 * Menace
 * When this creature enters, it endures 2. (Put two +1/+1 counters on it or
 * create a 2/2 white Spirit creature token.)
 */
val SandskitterOutrider = card("Sandskitter Outrider") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Soldier"
    power = 2
    toughness = 1
    oracleText = "Menace\n" +
        "When this creature enters, it endures 2. " +
        "(Put two +1/+1 counters on it or create a 2/2 white Spirit creature token.)"

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Endure(2)
        description = "When this creature enters, it endures 2."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "89"
        artist = "Arif Wijaya"
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c4bfebe-f10f-44bd-9368-33e273ba5a55.jpg?1743204318"
    }
}
