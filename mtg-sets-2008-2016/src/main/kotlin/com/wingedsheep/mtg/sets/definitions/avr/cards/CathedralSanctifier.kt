package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cathedral Sanctifier
 * {W}
 * Creature — Human Cleric
 * 1/1
 * When this creature enters, you gain 3 life.
 */
val CathedralSanctifier = card("Cathedral Sanctifier") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "When this creature enters, you gain 3 life."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Michael C. Hayes"
        flavorText = "\"Evil will soon be vanquished. What Innistrad most needs now is healing.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/7/6/76cac47a-9e83-4039-8d80-fa9bdadb7527.jpg?1783940738"
    }
}
