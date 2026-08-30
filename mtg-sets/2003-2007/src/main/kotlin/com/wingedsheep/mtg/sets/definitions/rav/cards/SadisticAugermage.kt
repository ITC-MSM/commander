package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sadistic Augermage
 * {2}{B}
 * Creature — Human Wizard
 * 3/1
 *
 * When this creature dies, each player puts a card from their hand on top of their library.
 *
 * A tuck, not a discard: the card goes to the top of its owner's library, so nothing here should
 * feed a discard trigger. Each player chooses their own card, in APNAP order.
 */
val SadisticAugermage = card("Sadistic Augermage") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard"
    oracleText = "When this creature dies, each player puts a card from their hand on top of their library."
    power = 3
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.EachPlayerPutsCardsOnTopOfLibrary()
        description = "When this creature dies, each player puts a card from their hand on top of their library."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Nick Percival"
        flavorText = "\"Don't worry. I know the drill.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40283989-c2cc-4f17-a817-2c548d2ce71f.jpg?1783943663"
    }
}
