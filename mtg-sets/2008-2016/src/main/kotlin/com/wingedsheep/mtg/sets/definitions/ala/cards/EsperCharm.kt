package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Esper Charm
 * {W}{U}{B}
 * Instant
 * Choose one —
 * • Destroy target enchantment.
 * • Draw two cards.
 * • Target player discards two cards.
 *
 * The Alara charm cycle: a `modal(chooseCount = 1)` block whose three modes each own their own
 * targets, so only the chosen mode's target is picked on the stack. Mode one is [Effects.Destroy]
 * (a graveyard move flagged `byDestruction`, so indestructible and regeneration still apply); mode
 * two is a bare [Effects.DrawCards]; mode three is [Patterns.Hand].discardCards pointed at the
 * targeted player — the gather → select → move pipeline, with the *target* doing the choosing.
 */
val EsperCharm = card("Esper Charm") {
    manaCost = "{W}{U}{B}"
    colorIdentity = "BUW"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Destroy target enchantment.\n" +
        "• Draw two cards.\n" +
        "• Target player discards two cards."

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target enchantment") {
                val t = target("target", TargetPermanent(filter = TargetFilter.Enchantment))
                effect = Effects.Destroy(t)
            }
            mode("Draw two cards") {
                effect = Effects.DrawCards(2)
            }
            mode("Target player discards two cards") {
                val t = target("target", TargetPlayer())
                effect = Patterns.Hand.discardCards(2, t)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "167"
        artist = "Michael Bruinsma"
        flavorText = "\"Thoughts are commodities. Someone will pay a good price for them. Even ones as simplistic as yours . . .\"\n—Ennor, mentalist"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3d70a07-8d91-462c-aa96-901cc9a81531.jpg"
    }
}
