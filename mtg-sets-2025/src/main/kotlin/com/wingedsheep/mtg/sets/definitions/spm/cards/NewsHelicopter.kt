package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * News Helicopter
 * {3}
 * Artifact Creature — Construct
 * 1/1
 * Flying
 * When this creature enters, create a 1/1 green and white Human Citizen creature token.
 */
val NewsHelicopter = card("News Helicopter") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Construct"
    oracleText = "Flying\nWhen this creature enters, create a 1/1 green and white Human Citizen creature token."
    power = 1
    toughness = 1
    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN, Color.WHITE),
            creatureTypes = setOf("Human", "Citizen")
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Lee Woo-chul"
        flavorText = "\"C'mon, we can't let Parker get all the shots of Spider-Man.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15717af0-30cd-4417-947a-c27cca06d93a.jpg?1757378055"
    }
}
