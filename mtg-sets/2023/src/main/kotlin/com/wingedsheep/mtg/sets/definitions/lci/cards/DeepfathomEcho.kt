package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deepfathom Echo — {2}{G}{U}
 * Creature — Merfolk Spirit
 * 4/4
 *
 * "At the beginning of combat on your turn, this creature explores. Then you may have it
 *  become a copy of another creature you control until end of turn."
 *
 * Implementation:
 *  - `trigger = Triggers.BeginCombat` fires at the start of combat on the controller's turn.
 *  - No target is declared at the `triggeredAbility` level. The "another creature you control"
 *    is selected mid-resolution — inside the `MayEffect` — via `Effects.SelectTarget`. This
 *    ensures the explore always runs unconditionally, and target selection happens only if the
 *    player accepts the copy step.
 *  - `Effects.Explore(EffectTarget.Self)` runs first (CR 701.44): reveals the top library card;
 *    a land goes to the hand, a nonland puts a +1/+1 counter on Deepfathom Echo and the
 *    controller may put that card into the graveyard.
 *  - `MayEffect(...)` wraps the copy step. The engine asks the controller yes/no; if yes:
 *    `Effects.SelectTarget(Targets.OtherCreatureYouControl, "copySource")` prompts for another
 *    creature the controller controls (Deepfathom Echo is excluded by `OtherCreatureYouControl`
 *    which carries `excludeSelf = true` via `TargetFilter.OtherCreatureYouControl`). When only
 *    one valid creature exists, the engine auto-selects it without a prompt.
 *  - `Effects.EachPermanentBecomesCopyOfTarget(target = PipelineTarget("copySource"),
 *    duration = EndOfTurn, affected = Self)` — single-permanent shape (`affected = Self`):
 *    Deepfathom Echo becomes a copy of the selected creature until end of turn, copiable values
 *    only (Rule 707). Counters, tapped state, attachments, and non-copy continuous effects are
 *    unaffected. End-of-turn cleanup restores Deepfathom Echo's original `CardComponent`
 *    snapshot via `CopyOfComponent`.
 */
val DeepfathomEcho = card("Deepfathom Echo") {
    manaCost = "{2}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Merfolk Spirit"
    power = 4
    toughness = 4
    oracleText = "At the beginning of combat on your turn, this creature explores. Then you may " +
        "have it become a copy of another creature you control until end of turn. (To have this " +
        "creature explore, reveal the top card of your library. Put that card into your hand if " +
        "it's a land. Otherwise, put a +1/+1 counter on this creature, then put the card back or " +
        "put it into your graveyard.)"

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = Effects.Composite(listOf(
            // Step 1: This creature explores (unconditional). Reveals top library card; land → hand,
            // nonland → +1/+1 counter on this creature + optional graveyard put (CR 701.44).
            Effects.Explore(EffectTarget.Self),
            // Step 2: The controller may have this creature become a copy of another creature
            // they control until end of turn. Target selection happens inside the MayEffect so
            // it is only asked when the player accepts, and does not bind at stack-placement time.
            MayEffect(
                Effects.SelectTarget(Targets.OtherCreatureYouControl, "copySource")
                    .then(Effects.EachPermanentBecomesCopyOfTarget(
                        target = EffectTarget.PipelineTarget("copySource"),
                        duration = Duration.EndOfTurn,
                        affected = EffectTarget.Self
                    ))
            )
        ))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "228"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7c7fb87-8448-49f4-a9ed-db97f6a41d98.jpg?1782694427"
    }
}
