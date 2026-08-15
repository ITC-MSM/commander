package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Foot Mystic
 * {3}{B}
 * Creature — Human Ninja Warlock
 * 2/4
 *
 * Lifelink
 * Disappear — When this creature enters, if a permanent left the battlefield
 * under your control this turn, create a 1/1 black Ninja creature token.
 */
val FootMystic = card("Foot Mystic") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Ninja Warlock"
    oracleText = "Lifelink\nDisappear — When this creature enters, if a permanent left the battlefield under your control this turn, create a 1/1 black Ninja creature token."
    power = 2
    toughness = 4

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Ninja"),
            imageUri = "https://cards.scryfall.io/normal/front/a/7/a7b76498-d696-40d1-b7c7-91657525b44f.jpg?1771590477"
        )
        description = "Disappear — When this creature enters, if a permanent left the battlefield under your control this turn, create a 1/1 black Ninja creature token."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "63"
        artist = "Irina Nordsol"
        flavorText = "Mashima would protect the soul of Oroku Saki at any cost."
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61e40a18-6cc8-436d-826b-1cf8cd037df3.jpg?1771586860"
    }
}
