package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Glimmervoid — Mirrodin #281
 * Land
 *
 * At the beginning of the end step, if you control no artifacts, sacrifice this land.
 * {T}: Add one mana of any color.
 *
 * A five-color land with an artifact-shaped leash, composed from two existing primitives.
 *
 * The mana half is [Effects.AddAnyColorMana] (one mana, one chosen color), marked
 * `manaAbility = true` with [TimingRule.ManaAbility] so it never uses the stack (CR 605.1a).
 *
 * The upkeep-of-the-drawback half is [Triggers.EachEndStep] — *each* end step, not just yours, so
 * an opponent's turn kills it just as fast — with the "if you control no artifacts" clause as an
 * intervening-if [triggerCondition]. That matters twice (CR 603.4): the ability doesn't even
 * trigger while you control an artifact, and if the last artifact leaves in response to the
 * trigger, the sacrifice still happens; conversely, deploying an artifact in response to the
 * trigger saves the land.
 *
 * Color identity stays colorless — "any color" adds nothing to identity (CR 903.4).
 */
val Glimmervoid = card("Glimmervoid") {
    manaCost = ""
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "At the beginning of the end step, if you control no artifacts, sacrifice this land.\n" +
        "{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EachEndStep
        triggerCondition = Conditions.YouControl(GameObjectFilter.Artifact, negate = true)
        effect = SacrificeSelfEffect
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "281"
        artist = "Lars Grant-West"
        flavorText = "An empty canvas holds infinite possibilities."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2ab98a1-664c-4775-a3dd-22a15e2f836b.jpg?1783944493"
    }
}
