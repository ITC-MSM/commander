package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.TargetOpponent

/**
 * Brutal Nightstalker
 * {3}{B}{B}
 * Creature — Nightstalker
 *
 * "Have target opponent discard a card" is the published gather → select → move recipe
 * [Patterns.Hand.discardCards] aimed at the bound target, so the discarding player both owns the
 * gathered hand and makes the choice.
 */
val BrutalNightstalker = card("Brutal Nightstalker") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightstalker"
    oracleText = "When this creature enters, you may have target opponent discard a card."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val opponent = target("target", TargetOpponent())
        effect = MayEffect(Patterns.Hand.discardCards(1, opponent))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "64"
        artist = "Brom"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b471102b-a66e-4cdc-b20f-7ae1f9bd0e8a.jpg"
    }
}
