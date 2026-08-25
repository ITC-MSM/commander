package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Ripjaw Raptor
 * {2}{G}{G}
 * Creature — Dinosaur
 * 4/5
 *
 * Enrage — Whenever this creature is dealt damage, draw a card.
 *
 * "Enrage" is an ability word (CR 207.2c) — no rules meaning, so it lives in the trigger's
 * description rather than as a keyword.
 */
val RipjawRaptor = card("Ripjaw Raptor") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Enrage — Whenever this creature is dealt damage, draw a card."
    power = 4
    toughness = 5

    triggeredAbility {
        trigger = Triggers.TakesDamage
        effect = Effects.DrawCards(1)
        description = "Enrage — Whenever this creature is dealt damage, draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "203"
        artist = "Ryan Pancoast"
        flavorText = "Raptors are clever enough to tear away a hard metal shell to get at the tasty morsel inside."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c5368c6-87e0-4f83-aaf7-f4af96d2bf30.jpg"
    }
}
