package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bombard
 * {2}{R}
 * Instant
 * Bombard deals 4 damage to target creature.
 *
 * Canonical printing: Rivals of Ixalan, the card's earliest real printing. ANB and EOE carry
 * `Printing` rows.
 */
val Bombard = card("Bombard") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Bombard deals 4 damage to target creature."

    spell {
        val victim = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(4, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "93"
        artist = "Alex Konstad"
        flavorText = "\"Want to sink a ship? Blow a hole in the hull. Want to kill a regisaur? " +
            "Same answer.\"\n—Captain Brandis Thorn"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a605abc-78e8-47ba-9022-0fad9006fd05.jpg?1783935304"
    }
}
