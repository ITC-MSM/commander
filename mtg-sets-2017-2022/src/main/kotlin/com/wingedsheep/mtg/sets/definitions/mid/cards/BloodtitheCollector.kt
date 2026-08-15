package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodtithe Collector
 * {4}{B}
 * Creature — Vampire Noble
 * 3/4
 *
 * Flying
 * When this creature enters, if an opponent lost life this turn, each opponent discards a card.
 *
 * The ETB discard is gated by an intervening "if" ([Conditions.OpponentLostLifeThisTurn], CR 603.4):
 * the trigger only goes on the stack — and only checked again on resolution — when an opponent has
 * lost life this turn. [Effects.EachOpponentDiscards] handles the per-opponent discard.
 *
 * Canonical printing lives here (Innistrad: Midnight Hunt, the earliest real printing); Foundations
 * reprints it (see the FDN `Printing` row).
 */
val BloodtitheCollector = card("Bloodtithe Collector") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Noble"
    power = 3
    toughness = 4
    oracleText = "Flying\n" +
        "When this creature enters, if an opponent lost life this turn, each opponent discards a card."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.OpponentLostLifeThisTurn
        effect = Effects.EachOpponentDiscards(1)
        description = "When this creature enters, if an opponent lost life this turn, each opponent " +
            "discards a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Maria Zolotukhina"
        flavorText = "For the more refined vampire, breaking a victim's will is far more satisfying " +
            "than simply taking their blood."
        imageUri = "https://cards.scryfall.io/normal/front/5/7/57d5e536-7774-4949-8127-727ae4d8fc80.jpg?1783925623"
    }
}
