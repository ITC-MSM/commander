package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fortress Kin-Guard — Tarkir: Dragonstorm #12
 * {1}{W} · Creature — Dog Soldier · 1/2
 *
 * When this creature enters, it endures 1. (Put a +1/+1 counter on it or
 * create a 1/1 white Spirit creature token.)
 */
val FortressKinGuard = card("Fortress Kin-Guard") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dog Soldier"
    power = 1
    toughness = 2
    oracleText = "When this creature enters, it endures 1. " +
        "(Put a +1/+1 counter on it or create a 1/1 white Spirit creature token.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Endure(1)
        description = "When this creature enters, it endures 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Daneen Wilkerson"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b647a018-1d70-43a1-a265-928bcd863689.jpg?1743204000"
    }
}
