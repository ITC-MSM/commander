package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * April O'Neil, Kunoichi Trainee
 * {1}{W}
 * Legendary Creature — Human Ninja
 * 2/2
 *
 * When April O'Neil enters, scry 2.
 * April O'Neil can't be blocked by creatures with power 3 or greater.
 */
val AprilONeilKunoichiTrainee = card("April O'Neil, Kunoichi Trainee") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Ninja"
    oracleText = "When April O'Neil enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)\nApril O'Neil can't be blocked by creatures with power 3 or greater."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(2)
    }

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtLeast(3))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Jo Cordisco"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b1982ea-686b-4acd-b677-47571430efb0.jpg?1771586740"
    }
}
