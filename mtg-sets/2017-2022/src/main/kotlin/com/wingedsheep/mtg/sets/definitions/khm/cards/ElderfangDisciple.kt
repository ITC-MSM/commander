package com.wingedsheep.mtg.sets.definitions.khm.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elderfang Disciple
 * {1}{B}
 * Creature — Elf Cleric
 * 1/1
 * When this creature enters, each opponent discards a card.
 *
 * [Effects.EachOpponentDiscards] is the shared gather -> choose -> discard pipeline, one iteration
 * per opponent, so each opponent picks their own card rather than the Disciple's controller picking
 * for them.
 */
val ElderfangDisciple = card("Elderfang Disciple") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elf Cleric"
    oracleText = "When this creature enters, each opponent discards a card."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.EachOpponentDiscards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Miranda Meeks"
        flavorText = "\"One day, the great serpent will rejoin us on Skemfar, and those who've wronged us will taste of our venom.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f3a6148-d005-49c1-a7fc-867c4e8251cd.jpg"
    }
}
