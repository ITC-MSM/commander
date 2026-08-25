package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Resounding Wave
 * {2}{U}
 * Instant
 * Return target permanent to its owner's hand.
 * Cycling {5}{W}{U}{B} ({5}{W}{U}{B}, Discard this card: Draw a card.)
 * When you cycle this card, return two target permanents to their owners' hands.
 *
 * The blue member of the Alara "Resounding" cycle, composed like the Onslaught cycling cycle: a
 * `spell { }` body, [KeywordAbility.cycling] for the wedge-coloured cycling cost, and a
 * [Triggers.YouCycleThis] triggered ability carrying the larger effect. The spell half is a single
 * [Effects.ReturnToHand]; the trigger declares a `count = 2` [TargetPermanent] requirement and fans
 * the bounce out with [ForEachTargetEffect] so each chosen permanent is moved independently — one
 * illegal target on resolution no longer costs the other its bounce. Unlike the Onslaught cycle
 * there is no printed "you may", so the trigger is not optional, and "two target permanents" is a
 * hard count rather than "up to two".
 */
val ResoundingWave = card("Resounding Wave") {
    manaCost = "{2}{U}"
    colorIdentity = "BUW"
    typeLine = "Instant"
    oracleText = "Return target permanent to its owner's hand.\n" +
        "Cycling {5}{W}{U}{B} ({5}{W}{U}{B}, Discard this card: Draw a card.)\n" +
        "When you cycle this card, return two target permanents to their owners' hands."

    spell {
        val t = target("target", Targets.Permanent)
        effect = Effects.ReturnToHand(t)
    }

    keywordAbility(KeywordAbility.cycling("{5}{W}{U}{B}"))

    triggeredAbility {
        trigger = Triggers.YouCycleThis
        target("target", TargetPermanent(count = 2))
        effect = ForEachTargetEffect(
            effects = listOf(Effects.ReturnToHand(EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Izzy"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3eca679-076d-42f0-9ec0-b8116a94e373.jpg"
    }
}
