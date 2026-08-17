package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hornet Queen
 * {4}{G}{G}{G}
 * Creature — Insect
 * 2/2
 * Flying, deathtouch
 * When this creature enters, create four 1/1 green Insect creature tokens with flying and deathtouch.
 *
 * A single [Effects.CreateToken] carries the whole card: the token's colors, creature types and
 * granted keywords are parameters of that one effect, and `count = 4` makes the swarm one effect
 * rather than four — no repetition primitive is needed.
 */
val HornetQueen = card("Hornet Queen") {
    manaCost = "{4}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 2
    toughness = 2
    oracleText = "Flying, deathtouch\n" +
        "When this creature enters, create four 1/1 green Insect creature tokens with flying and deathtouch."

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Insect"),
            keywords = setOf(Keyword.FLYING, Keyword.DEATHTOUCH),
            count = 4
        )
        description = "When this creature enters, create four 1/1 green Insect creature tokens " +
            "with flying and deathtouch."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "159"
        artist = "Martina Pilcerova"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/714cd47b-88f6-448d-9242-481935565250.jpg?1783941193"
    }
}
