package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pitchburn Devils
 * {4}{R}
 * Creature — Devil
 * 3 / 3
 * When this creature dies, it deals 3 damage to any target.
 *
 * Canonical printing: Innistrad, the card's earliest real printing. Reprinted in Magic 2014.
 */
val PitchburnDevils = card("Pitchburn Devils") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Devil"
    power = 3
    toughness = 3
    oracleText = "When this creature dies, it deals 3 damage to any target."

    triggeredAbility {
        trigger = Triggers.Dies
        val damaged = target("target", Targets.Any)
        effect = Effects.DealDamage(3, damaged)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "156"
        artist = "Johann Bodin"
        flavorText = "The ingenuity of goblins, the depravity of demons, and the smarts of sheep."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d31d3de5-4028-457f-8eba-82e829061a40.jpg"
    }
}
