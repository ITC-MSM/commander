package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Insectoid Exterminator
 * {2}{B}
 * Creature — Insect Mutant
 * 2/2
 *
 * Flying
 * Disappear — At the beginning of your end step, if a permanent left the
 * battlefield under your control this turn, scry 1.
 */
val InsectoidExterminator = card("Insectoid Exterminator") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect Mutant"
    oracleText = "Flying\nDisappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, scry 1. (Look at the top card of your library. You may put that card on the bottom.)"
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        effect = Patterns.Library.scry(1)
        description = "Disappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "64"
        artist = "Brian Valeza"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff26f7ff-7f70-4204-9b48-de9e21dc89ec.jpg?1771586866"
    }
}
