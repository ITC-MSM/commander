package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur Healer
 * {1}{G}{W}
 * Creature — Centaur Cleric
 * 3/3
 *
 * When this creature enters, you gain 3 life.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * An enters trigger over [Effects.GainLife], whose default target is the controller.
 */
val CentaurHealer = card("Centaur Healer") {
    manaCost = "{1}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Centaur Cleric"
    oracleText = "When this creature enters, you gain 3 life."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "Mark Zug"
        flavorText = "Instructors at the Kasarna training grounds are capable healers in case their students fail to grasp the subtleties of combat."
        imageUri = "https://cards.scryfall.io/normal/front/8/3/833835d1-9beb-4ad8-b675-7adebdbd7d82.jpg?1783940343"
    }
}
