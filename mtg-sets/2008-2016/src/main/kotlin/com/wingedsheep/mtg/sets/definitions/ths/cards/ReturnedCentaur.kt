package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Returned Centaur
 * {3}{B}
 * Creature — Zombie Centaur
 * 2/4
 *
 * When this creature enters, target player mills four cards.
 */
val ReturnedCentaur = card("Returned Centaur") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Centaur"
    oracleText = "When this creature enters, target player mills four cards."
    power = 2
    toughness = 4

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val p = target("target player", Targets.Player)
        effect = Patterns.Library.mill(4, p)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Lucas Graciano"
        flavorText = "Driven away by his living kin, he wanders mourning through the wilderness, seeking the dead city of Asphodel."
        imageUri = "https://cards.scryfall.io/normal/front/1/0/1067cf71-a270-4b1b-aa06-0ec0e732ed16.jpg"
    }
}
