package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jace Beleren - {1}{U}{U}
 * Legendary Planeswalker — Jace
 * Starting Loyalty: 3
 *
 * +2: Each player draws a card.
 *
 * −1: Target player draws a card.
 *
 * −10: Target player mills twenty cards.
 *
 * The +2 is symmetric — Jace's controller draws too — so it is a draw aimed at [Player.Each], not
 * at each opponent. The −1 and −10 both target a *player*, which is what lets the −1 be pointed
 * at yourself and the −10 at an opponent whose library is short (they simply mill what's left,
 * per the 2009-10-01 ruling).
 */
val JaceBeleren = card("Jace Beleren") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Planeswalker — Jace"
    startingLoyalty = 3
    oracleText = "+2: Each player draws a card.\n" +
        "−1: Target player draws a card.\n" +
        "−10: Target player mills twenty cards."

    // +2: Each player draws a card.
    loyaltyAbility(+2) {
        effect = Effects.DrawCards(1, EffectTarget.PlayerRef(Player.Each))
    }

    // −1: Target player draws a card.
    loyaltyAbility(-1) {
        val player = target("target player", Targets.Player)
        effect = Effects.DrawCards(1, player)
    }

    // −10: Target player mills twenty cards.
    loyaltyAbility(-10) {
        val player = target("target player", Targets.Player)
        effect = Patterns.Library.mill(20, player)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdffb058-1af0-41bb-956a-ae10e092c389.jpg?1783942901"
        ruling(
            "2009-10-01",
            "If there are fewer than twenty cards in the targeted player's library, that player " +
                "puts all the cards from their library into their graveyard.",
        )
    }
}
