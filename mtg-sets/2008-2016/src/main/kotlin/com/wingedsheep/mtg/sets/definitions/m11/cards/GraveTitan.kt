package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grave Titan
 * {4}{B}{B}
 * Creature — Giant
 * 6/6
 *
 * Deathtouch
 * Whenever this creature enters or attacks, create two 2/2 black Zombie creature tokens.
 *
 * - "Enters **or** attacks" is not a single trigger event: it is two triggered abilities sharing
 *   one effect, the same shape Haliya, Ascendant Cadet uses.
 * - The tokens are unnamed 2/2 black Zombies — the token has no printed name of its own, so no
 *   `name` is passed and the engine derives it from the creature type.
 */
val GraveTitan = card("Grave Titan") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Giant"
    power = 6
    toughness = 6
    oracleText = "Deathtouch\n" +
        "Whenever this creature enters or attacks, create two 2/2 black Zombie creature tokens."

    keywords(Keyword.DEATHTOUCH)

    val tokenDescription = "Whenever this creature enters or attacks, create two 2/2 black " +
        "Zombie creature tokens."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            count = 2,
        )
        description = tokenDescription
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            count = 2,
        )
        description = tokenDescription
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "97"
        artist = "Nils Hamm"
        flavorText = "Death in form and function."
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5fa6d385-6b8e-45ad-83dc-b477799c05a5.jpg?1783941816"
    }
}
