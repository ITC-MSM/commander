package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.costs.PayCost
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Perforating Artist
 * {1}{B}{R}
 * Creature — Devil
 * 3/2
 *
 * Deathtouch
 * Raid — At the beginning of your end step, if you attacked this turn, each opponent
 * loses 3 life unless that player sacrifices a nonland permanent of their choice or
 * discards a card.
 *
 * Raid (ability word, CR 207.2c — flavor only) is the intervening-if
 * [Conditions.YouAttackedThisTurn] on the end-step trigger (Rule 603.4). The payoff is
 * per-opponent: [Effects.ForEachPlayer] over [Player.EachOpponent] rebinds the body's
 * controller to each opponent, so [EffectTarget.Controller] inside resolves to *that*
 * opponent. Each opponent independently faces a [PayOrSufferEffect] — pay by choosing to
 * sacrifice a nonland permanent or discard a card ([PayCost.Choice]), else lose 3 life.
 * This mirrors Starseer Mentor's punisher, widened from "target opponent" to "each opponent".
 */
val PerforatingArtist = card("Perforating Artist") {
    manaCost = "{1}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Devil"
    power = 3
    toughness = 2
    oracleText = "Deathtouch\nRaid — At the beginning of your end step, if you attacked this turn, " +
        "each opponent loses 3 life unless that player sacrifices a nonland permanent of their choice " +
        "or discards a card."

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouAttackedThisTurn
        effect = Effects.ForEachPlayer(
            Player.EachOpponent,
            listOf(
                PayOrSufferEffect(
                    cost = Costs.pay.Choice(
                        listOf(
                            Costs.pay.Sacrifice(filter = GameObjectFilter.Nonland),
                            Costs.pay.Discard()
                        )
                    ),
                    suffer = LoseLifeEffect(
                        amount = DynamicAmount.Fixed(3),
                        target = EffectTarget.Controller
                    ),
                    player = EffectTarget.Controller
                )
            )
        )
        description = "Raid — At the beginning of your end step, if you attacked this turn, each " +
            "opponent loses 3 life unless that player sacrifices a nonland permanent of their choice " +
            "or discards a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "124"
        artist = "Arif Wijaya"
        flavorText = "The first three rows of the audience are marketed as the \"slash zone.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72980409-53f0-43c1-965e-06f22e7bb608.jpg?1782689158"
    }
}
