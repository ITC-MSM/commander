package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Lowland Oaf
 * {3}{R}
 * Creature — Giant Warrior
 * 3/3
 * {T}: Target Goblin creature you control gets +1/+0 and gains flying until end of turn. Sacrifice
 * that creature at the beginning of the next end step.
 *
 * The Giant throws a Goblin, and the Goblin does not survive it. The delayed sacrifice is the
 * Skirk Alarmist shape: `CreateDelayedTriggerEffect(step = END)` over
 * `Effects.SacrificeTarget(<the same bound target>)`. `CreateDelayedTriggerExecutor` bakes the
 * chosen target into a concrete entity id when the trigger is *scheduled*, so the sacrifice hits
 * the creature that was launched even though the ability's execution context is long gone.
 *
 * The target is "Goblin **creature** you control" — a noncreature Goblin permanent (a Goblin
 * Kindred artifact, say) is not a legal target, which is why this is `TargetCreature` over a
 * subtype-narrowed filter rather than a bare `Permanent.withSubtype`.
 *
 * If the creature has already left the battlefield when the end step arrives, the delayed trigger
 * simply finds nothing to sacrifice; and activating this during an end step pushes the sacrifice to
 * the *next* one, a turn later.
 */
val LowlandOaf = card("Lowland Oaf") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 3
    toughness = 3
    oracleText = "{T}: Target Goblin creature you control gets +1/+0 and gains flying until end of " +
        "turn. Sacrifice that creature at the beginning of the next end step."

    activatedAbility {
        cost = Costs.Tap
        val goblin = target(
            "target Goblin creature you control",
            TargetCreature(filter = TargetFilter.Creature.withSubtype(Subtype.GOBLIN).youControl())
        )
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, goblin),
            Effects.GrantKeyword(Keyword.FLYING, goblin, Duration.EndOfTurn),
            CreateDelayedTriggerEffect(
                step = Step.END,
                effect = Effects.SacrificeTarget(goblin)
            )
        )
        description = "Target Goblin creature you control gets +1/+0 and gains flying until end of " +
            "turn. Sacrifice that creature at the beginning of the next end step."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "184"
        artist = "Jeff Easley"
        flavorText = "\"I don't know why the little one was so mad. He said to put him down, and I put him down.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cdaa7f6a-9639-4bee-aa12-20054d7975d5.jpg?1783942871"
    }
}
