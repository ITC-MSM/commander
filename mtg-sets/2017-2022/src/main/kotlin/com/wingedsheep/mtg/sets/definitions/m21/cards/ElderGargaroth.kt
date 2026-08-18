package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect

/**
 * Elder Gargaroth
 * {3}{G}{G}
 * Creature — Beast
 * 6/6
 * Reach, vigilance, trample
 * Whenever this creature attacks or blocks, choose one —
 * • Create a 3/3 green Beast creature token.
 * • You gain 3 life.
 * • Draw a card.
 */
val ElderGargaroth = card("Elder Gargaroth") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    oracleText = "Reach, vigilance, trample\n" +
        "Whenever this creature attacks or blocks, choose one —\n" +
        "• Create a 3/3 green Beast creature token.\n" +
        "• You gain 3 life.\n" +
        "• Draw a card."
    power = 6
    toughness = 6

    keywords(Keyword.REACH, Keyword.VIGILANCE, Keyword.TRAMPLE)

    val attackOrBlockChoice = ModalEffect.chooseOne(
        Mode.noTarget(
            Effects.CreateToken(
                power = 3,
                toughness = 3,
                colors = setOf(Color.GREEN),
                creatureTypes = setOf("Beast"),
                imageUri = "https://cards.scryfall.io/normal/front/8/8/882247ba-99d2-46db-8314-f800f3366b7f.jpg?1783930590"
            ),
            "Create a 3/3 green Beast creature token"
        ),
        Mode.noTarget(
            Effects.GainLife(3),
            "You gain 3 life"
        ),
        Mode.noTarget(
            Effects.DrawCards(1),
            "Draw a card"
        )
    )

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = attackOrBlockChoice
    }

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = attackOrBlockChoice
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "179"
        artist = "Nicholas Gregory"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d51269cf-a333-4a64-94cd-245798d840d2.jpg?1783930678"
    }
}
