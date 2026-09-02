package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stonebound Mentor — Strixhaven: School of Mages #239 (canonical printing)
 * {1}{R}{W} · Creature — Spirit Advisor · 3/3
 *
 * Whenever one or more cards leave your graveyard, scry 1.
 *
 * "One or more … leave" is CR 603.2c batch wording, so the batching
 * [Triggers.CardsLeaveYourGraveyard] is the right shape: a mass reanimation or a graveyard-exiling
 * sweep fires this exactly once. The payoff is a bare [Effects.Scry].
 */
val StoneboundMentor = card("Stonebound Mentor") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Spirit Advisor"
    oracleText =
        "Whenever one or more cards leave your graveyard, scry 1."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.CardsLeaveYourGraveyard()
        effect = Effects.Scry(1)
        description = "Whenever one or more cards leave your graveyard, scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "239"
        artist = "Svetlin Velinov"
        flavorText = "Quintorius beamed, his mind eager to absorb every historical fact the spirit wanted to divulge."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c64e954-adfc-40a2-a3b2-85f1b4626976.jpg?1783927289"
    }
}
