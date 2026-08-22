package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SwapBlockingAssignmentsEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/** Blocking creatures are the only legal targets for the swap. */
private val blockingCreature = GameObjectFilter.Creature.blocking()

/**
 * Sorrow's Path
 * Land
 * {T}: Choose two target blocking creatures controlled by the same opponent. If each of those
 * creatures could block all creatures that the other is blocking, remove both of them from combat.
 * Each one then blocks all creatures the other was blocking.
 * Whenever this land becomes tapped, it deals 2 damage to you and each creature you control.
 *
 * The famously unusable one. Both halves are implemented as printed, including the part that makes
 * it unusable.
 *
 * The swap's gate is a real legality check, not a formality: each creature must be able to block
 * *every* attacker the other is currently blocking, run through the same `defaultBlockEvasionRules`
 * a declared block goes through. A creature that couldn't have blocked a flier by declaring can't
 * be handed one here either, and when either direction fails the effect does nothing at all rather
 * than half-swapping. Approximating that away would remove the reason the card is interesting.
 *
 * The second ability keys off *becoming tapped*, so it fires when the first is activated — which is
 * the other half of the joke, and why the two are written on one card.
 */
val SorrowsPath = card("Sorrow's Path") {
    typeLine = "Land"
    oracleText = "{T}: Choose two target blocking creatures controlled by the same opponent. If " +
        "each of those creatures could block all creatures that the other is blocking, remove " +
        "both of them from combat. Each one then blocks all creatures the other was blocking.\n" +
        "Whenever this land becomes tapped, it deals 2 damage to you and each creature you control."

    activatedAbility {
        cost = Costs.Tap
        target("first blocker", TargetCreature(filter = TargetFilter(blockingCreature)))
        target("second blocker", TargetCreature(filter = TargetFilter(blockingCreature)))
        effect = SwapBlockingAssignmentsEffect
        description = "{T}: Choose two target blocking creatures controlled by the same opponent. " +
            "If each of those creatures could block all creatures that the other is blocking, " +
            "remove both of them from combat. Each one then blocks all creatures the other was " +
            "blocking."
    }

    triggeredAbility {
        trigger = Triggers.BecomesTapped
        effect = Effects.Composite(
            Effects.DealDamage(2, EffectTarget.Controller),
            Patterns.Group.dealDamageToAll(2, Filters.Group.creaturesYouControl),
        )
        description = "Whenever this land becomes tapped, it deals 2 damage to you and each " +
            "creature you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "119"
        artist = "Randy Asplund-Faith"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f75946b-1690-43cc-993c-d4e451a1a41c.jpg?1783947922"
    }
}
