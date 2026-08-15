package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Lifecreed Duo
 * {1}{W}
 * Creature — Bat Bird
 * 1/2
 *
 * Flying
 * Whenever another creature you control enters, you gain 1 life.
 */
val LifecreedDuo = card("Lifecreed Duo") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bat Bird"
    power = 1
    toughness = 2
    oracleText = "Flying\nWhenever another creature you control enters, you gain 1 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "20"
        artist = "Lorenzo Mastroianni"
        flavorText = "\"Why are you eating upside down?\" asked the bird. \"Why are you eating upside up?\" replied the bat."
        imageUri = "https://cards.scryfall.io/normal/front/c/a/ca543405-5e12-48a0-9a77-082ac9bcb2f2.jpg?1721425877"
    }
}
