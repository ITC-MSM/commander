package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Resounding Roar
 * {1}{G}
 * Instant
 * Target creature gets +3/+3 until end of turn.
 * Cycling {5}{R}{G}{W} ({5}{R}{G}{W}, Discard this card: Draw a card.)
 * When you cycle this card, target creature gets +6/+6 until end of turn.
 *
 * The Alara "Resounding" shape, and the same three-part composition as the Onslaught cycling cycle:
 * a `spell { }` body, [KeywordAbility.cycling] for the expensive wedge-coloured cycling cost, and a
 * [Triggers.YouCycleThis] triggered ability carrying the larger effect. Both halves are
 * [Effects.ModifyStats], whose default `Duration.EndOfTurn` is the printed "until end of turn", and
 * each declares its own creature target — the trigger targets when it goes on the stack, so the
 * cycled card's own spell target is never involved. Unlike the Onslaught cycle there is no printed
 * "you may", so the trigger is not optional.
 */
val ResoundingRoar = card("Resounding Roar") {
    manaCost = "{1}{G}"
    colorIdentity = "GRW"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+3 until end of turn.\n" +
        "Cycling {5}{R}{G}{W} ({5}{R}{G}{W}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, target creature gets +6/+6 until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(3, 3, t)
    }

    keywordAbility(KeywordAbility.cycling("{5}{R}{G}{W}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(6, 6, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/2/1/217bf237-a758-4500-b221-5e88fed9d0fc.jpg"
    }
}
