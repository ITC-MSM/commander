package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion

/**
 * Oblivious Bookworm
 * {G}{U}
 * Creature — Human Wizard
 * 2/3
 * At the beginning of your end step, you may draw a card. If you do, discard a card unless a
 *   permanent entered the battlefield face down under your control this turn or you turned a
 *   permanent face up this turn.
 *
 * Modeled as: end-step trigger → MayEffect (the optional draw) → IfYouDo (gate the rider on the
 * draw actually happening) → a discard that is itself gated to run only when neither face-down
 * condition holds ("...unless..."). Per the Scryfall ruling, the unless-check is evaluated as the
 * ability resolves, so the per-turn trackers reflect everything that happened earlier this turn.
 */
val ObliviousBookworm = card("Oblivious Bookworm") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Human Wizard"
    oracleText = "At the beginning of your end step, you may draw a card. If you do, discard a " +
        "card unless a permanent entered the battlefield face down under your control this turn " +
        "or you turned a permanent face up this turn."
    power = 2
    toughness = 3

    triggeredAbility {
        trigger = Triggers.YourEndStep
        // The "may" already represents the optional draw ("you may draw a card"); the IfYouDo
        // gates the discard rider on the draw actually happening ("If you do, …"). Auto can't
        // infer success from a bare draw, so the criterion is explicit.
        effect = MayEffect(
            Effects.IfYouDo(
                action = Effects.DrawCards(1),
                // "discard a card unless [a permanent entered face down OR you turned one face up this turn]"
                ifYouDo = ConditionalEffect(
                    condition = Conditions.Not(
                        Conditions.Any(
                            Conditions.PermanentEnteredFaceDownThisTurn,
                            Conditions.YouTurnedPermanentFaceUpThisTurn
                        )
                    ),
                    effect = Effects.Discard(1)
                ),
                successCriterion = SuccessCriterion.Always
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Josh Newton"
        flavorText = "\"Huh, the detector won't stop beeping. Must be on the fritz again.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7b4c50b-fe76-430d-8f96-208f15ca4cd7.jpg?1726286707"
    }
}
