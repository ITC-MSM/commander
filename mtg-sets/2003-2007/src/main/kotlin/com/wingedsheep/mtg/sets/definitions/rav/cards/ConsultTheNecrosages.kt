package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Consult the Necrosages
 * {1}{U}{B}
 * Sorcery
 * Choose one —
 * • Target player draws two cards.
 * • Target player discards two cards.
 *
 * Each mode carries its own target requirement: the modes are separately targeted, so the chosen
 * player is bound per mode rather than once for the spell.
 */
val ConsultTheNecrosages = card("Consult the Necrosages") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Target player draws two cards.\n" +
        "• Target player discards two cards."

    spell {
        modal {
            mode("Target player draws two cards.") {
                val p = target("target player", Targets.Player)
                effect = Effects.DrawCards(2, p)
            }
            mode("Target player discards two cards.") {
                val p = target("target player", Targets.Player)
                effect = Patterns.Hand.discardCards(2, p)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "199"
        artist = "Paolo Parente"
        flavorText = "Dimir rank and file never see nor hear their guildmaster. All orders are given through mysterious necrosages who appear from the shadows, tersely toss out a command, and then melt into the darkness."
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f51484b-3bad-4332-87f8-61924e153799.jpg"
    }
}
