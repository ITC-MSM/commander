package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Pack Guardian
 * {2}{G}{G}
 * Creature — Wolf Spirit
 * 4/3
 * Flash
 * When this creature enters, you may discard a land card. If you do, create a 2/2 green Wolf
 * creature token.
 */
val PackGuardian = card("Pack Guardian") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf Spirit"
    power = 4
    toughness = 3
    oracleText = "Flash\n" +
        "When this creature enters, you may discard a land card. If you do, create a 2/2 green " +
        "Wolf creature token."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            effect = IfYouDoEffect(
                action = Patterns.Hand.discardCards(1, filter = GameObjectFilter.Land),
                ifYouDo = Effects.CreateToken(
                    power = 2,
                    toughness = 2,
                    colors = setOf(Color.GREEN),
                    creatureTypes = setOf("Wolf")
                )
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Filip Burburan"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64dcc129-88c2-4f20-be0b-36c4141688c9.jpg?1782712003"
    }
}
