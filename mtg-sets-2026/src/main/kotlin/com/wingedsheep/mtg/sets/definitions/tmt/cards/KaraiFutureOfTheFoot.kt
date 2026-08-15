package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.sneak
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Karai, Future of the Foot
 * {1}{W}{B}
 * Legendary Creature — Human Ninja
 * 3/3
 *
 * Sneak {2}{W}{B} (You may cast this spell for {2}{W}{B} if you also return an unblocked
 * attacker you control to hand during the declare blockers step. She enters tapped and
 * attacking.)
 * Whenever Karai deals combat damage to a player, return target creature card from your
 * graveyard to your hand. If her sneak cost was paid this turn, instead return that card to
 * the battlefield.
 *
 * The combat-damage trigger composes a single [ConditionalEffect]: the default branch returns
 * the chosen creature card to hand ([Effects.ReturnToHand]); when the "instead" condition holds
 * it goes to the battlefield ([Effects.PutOntoBattlefield] — it's your own graveyard card, so
 * with no controller override it enters under your control).
 *
 * "If her sneak cost was paid this turn" — the durable [Conditions.SneakCostWasPaid] flag on the
 * permanent (CR 702.190) records that the sneak cost was paid, but it never clears, while the
 * printed clause is turn-scoped. A creature can only be put onto the battlefield by its sneak
 * cost on the very turn it's sneaked in, so ANDing the flag with [Conditions.SourceEnteredThisTurn]
 * exactly reproduces "this turn": both are true only on the sneak turn (when Karai entered tapped
 * and attacking and immediately deals combat damage), and on every later turn she attacks normally
 * `SourceEnteredThisTurn` is false, so the card correctly returns to hand instead.
 */
val KaraiFutureOfTheFoot = card("Karai, Future of the Foot") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Human Ninja"
    oracleText = "Sneak {2}{W}{B} (You may cast this spell for {2}{W}{B} if you also return an unblocked attacker you control to hand during the declare blockers step. She enters tapped and attacking.)\nWhenever Karai deals combat damage to a player, return target creature card from your graveyard to your hand. If her sneak cost was paid this turn, instead return that card to the battlefield."
    power = 3
    toughness = 3

    sneak("{2}{W}{B}")

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        val creatureCard = target("target creature card in your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = ConditionalEffect(
            condition = Conditions.All(Conditions.SneakCostWasPaid, Conditions.SourceEnteredThisTurn),
            effect = Effects.PutOntoBattlefield(creatureCard),
            elseEffect = Effects.ReturnToHand(creatureCard)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Lius Lasahido"
        imageUri = "https://cards.scryfall.io/normal/front/0/d/0dded2d4-1640-4431-809e-403b51b27db6.jpg?1771342417"
    }
}
