package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Piracy Charm
 * {U}
 * Instant
 * Choose one —
 * • Target creature gains islandwalk until end of turn.
 * • Target creature gets +2/-1 until end of turn.
 * • Target player discards a card.
 *
 * The discard mode passes the bound target into [Patterns.Hand.discardCards], which is what makes
 * the *targeted player* both the source of the hand and the chooser.
 */
val PiracyCharm = card("Piracy Charm") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Target creature gains islandwalk until end of turn. (It can't be blocked as long as defending player controls an Island.)\n" +
        "• Target creature gets +2/-1 until end of turn.\n" +
        "• Target player discards a card."

    spell {
        modal(chooseCount = 1) {
            mode("Target creature gains islandwalk until end of turn") {
                val t = target("target", Targets.Creature)
                effect = Effects.GrantKeyword(Keyword.ISLANDWALK, t)
            }
            mode("Target creature gets +2/-1 until end of turn") {
                val t = target("target", Targets.Creature)
                effect = Effects.ModifyStats(2, -1, t)
            }
            mode("Target player discards a card") {
                val p = target("target", Targets.Player)
                effect = Patterns.Hand.discardCards(1, p)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "58"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/5/8/586a1d9f-59ae-41a7-8de7-d6c4553ea79e.jpg"
    }
}
