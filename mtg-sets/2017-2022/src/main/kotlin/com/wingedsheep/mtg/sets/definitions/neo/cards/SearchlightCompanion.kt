package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Searchlight Companion — Kamigawa: Neon Dynasty #258 (canonical printing)
 * {3} · Artifact Creature — Drone · 1/1
 *
 * Flying
 * When this creature enters, create a 1/1 colorless Spirit creature token.
 *
 * The Spirit token is colorless, not white — NEO's artifact Spirits carry no colour, so the
 * token's `colors` set stays empty.
 */
val SearchlightCompanion = card("Searchlight Companion") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Drone"
    power = 1
    toughness = 1
    oracleText = "Flying\nWhen this creature enters, create a 1/1 colorless Spirit creature token."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Spirit"),
        )
        description = "When this creature enters, create a 1/1 colorless Spirit creature token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "258"
        artist = "Rudy Siswanto"
        flavorText = "The merging of the spirit and material realms has resulted in some unusual " +
            "friendships."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b79b30e-e7aa-490e-b130-de7533e6e13b.jpg?1783923821"
    }
}
