package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect

/**
 * Transluminant
 * {1}{G}
 * Creature — Dryad Shaman
 * 2/2
 *
 * {W}, Sacrifice this creature: Create a 1/1 white Spirit creature token with flying at the
 * beginning of the next end step.
 *
 * The token is not created on resolution — the ability schedules a step-based delayed trigger for
 * the next beginning-of-end-step, which is what makes the printed ruling true: activating during
 * a turn's end step has already missed that step's trigger window, so the Spirit arrives at the
 * *following* turn's end step. `fireOnPlayer` stays null because the text says "the next end
 * step", not "your next end step".
 *
 * Ravnica predates token *cards* (Scryfall has no `trav` set), and the set self-hosts art only
 * for its Saproling, so this token falls through to the engine-wide generic Spirit art.
 */
val Transluminant = card("Transluminant") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad Shaman"
    power = 2
    toughness = 2
    oracleText = "{W}, Sacrifice this creature: Create a 1/1 white Spirit creature token with " +
        "flying at the beginning of the next end step."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.SacrificeSelf)
        effect = CreateDelayedTriggerEffect(
            step = Step.END,
            effect = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Spirit"),
                keywords = setOf(Keyword.FLYING),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Greg Hildebrandt"
        flavorText = "\"Forget yourself. Forget your city. Forget your homes, your families, the " +
            "debts and obligations that hold you to this world.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/1/318ce4ef-38bd-4360-895f-457587164197.jpg?1783943629"
        ruling(
            "2005-10-01",
            "If you use this ability during a turn's end phase, the chance to put \"at end of " +
                "turn\"-triggered abilities on the stack has passed. You won't get the Spirit " +
                "creature token until the beginning of the next end step."
        )
    }
}
