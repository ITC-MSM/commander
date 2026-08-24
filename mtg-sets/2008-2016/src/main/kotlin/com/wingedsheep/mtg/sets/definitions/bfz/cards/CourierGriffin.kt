package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Courier Griffin
 * {3}{W}
 * Creature — Griffin
 * 2/3
 * Flying
 * When this creature enters, you gain 2 life.
 */
val CourierGriffin = card("Courier Griffin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Griffin"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "When this creature enters, you gain 2 life."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "21"
        artist = "Kieran Yanner"
        flavorText = "\"Sea Gate has fallen. Survivors are on the move. Will send another griffin when we find " +
            "refuge. Stay hidden. Stay safe.\"\n" +
            "—Message from Tars Olan, kor world-gift"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d3afc71-f5db-45c3-96b2-8454b7f33542.jpg?1783938222"
    }
}
