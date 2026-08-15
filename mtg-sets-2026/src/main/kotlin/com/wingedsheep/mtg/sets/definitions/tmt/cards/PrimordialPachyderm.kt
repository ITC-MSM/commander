package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Primordial Pachyderm
 * {3}{G}
 * Creature — Elephant Avatar
 * 4/4
 *
 * Reach, trample
 * When this creature enters, you gain 2 life.
 */
val PrimordialPachyderm = card("Primordial Pachyderm") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elephant Avatar"
    oracleText = "Reach, trample\nWhen this creature enters, you gain 2 life."
    power = 4
    toughness = 4

    keywords(Keyword.REACH, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"Have at me, and we will find out who is the fittest among us.\"\n—Manmoth"
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1a866e6-4108-4290-9680-8f1652fbcf77.jpg?1771502730"
    }
}
