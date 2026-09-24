package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerTiming
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Gaze of the Gorgon — Ravnica: City of Guilds #246
 * {3}{B/G} · Instant
 *
 * Regenerate target creature. At this turn's next end of combat, destroy all creatures that
 * blocked or were blocked by it this turn.
 *
 * The regeneration is the ordinary shield. The second sentence is a step-based delayed trigger at
 * [Step.END_COMBAT] with [DelayedTriggerTiming.THIS_TURN_ONLY] — "this turn's *next* end of
 * combat", so cast after the last combat of the turn it is simply dropped at end of turn. Its
 * `watchedTarget` bakes in the targeted creature, which the fired trigger exposes as its triggering
 * entity; the destroy filter then reads every creature's turn-scoped combat-partner record for it
 * ([GameObjectFilter.blockedOrWasBlockedByThisTurn] over [EntityReference.Triggering]). That record
 * is written at block declaration on both sides and kept until the turn ends, so — per the ruling —
 * creatures that blocked it before the Gaze was cast are included, and it does not matter whether
 * the targeted creature is still on the battlefield when the trigger resolves.
 */
val GazeOfTheGorgon = card("Gaze of the Gorgon") {
    manaCost = "{3}{B/G}"
    colorIdentity = "BG"
    typeLine = "Instant"
    oracleText = "({B/G} can be paid with either {B} or {G}.)\n" +
        "Regenerate target creature. At this turn's next end of combat, destroy all creatures that blocked " +
        "or were blocked by it this turn."

    spell {
        val gazer = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.Regenerate(gazer),
            CreateDelayedTriggerEffect(
                step = Step.END_COMBAT,
                timing = DelayedTriggerTiming.THIS_TURN_ONLY,
                watchedTarget = gazer,
                effect = Effects.DestroyAll(
                    GameObjectFilter.Creature.blockedOrWasBlockedByThisTurn(EntityReference.Triggering)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "246"
        artist = "Stephen Tappin"
        flavorText = "Even the dead give way to the mineral gaze of the gorgon."
        imageUri = "https://cards.scryfall.io/normal/front/1/6/16c9e772-5213-4c28-9b6c-1cfa6675ee8f.jpg?1783943604"
        ruling("2005-10-01", "At the next end of combat step in the turn, all creatures that had blocked the targeted creature at any point during the turn will be destroyed. This includes creatures that blocked it before Gaze of the Gorgon was cast. It doesn’t matter whether the targeted creature is still on the battlefield at that point.")
    }
}
