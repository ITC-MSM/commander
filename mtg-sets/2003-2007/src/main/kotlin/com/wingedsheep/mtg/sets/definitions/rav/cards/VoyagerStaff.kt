package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Voyager Staff
 * {1}
 * Artifact
 *
 * {2}, Sacrifice this artifact: Exile target creature. Return the exiled card to the battlefield
 * under its owner's control at the beginning of the next end step.
 *
 * The plain blink pattern — [Patterns.Exile.exileUntilEndStep] moves the target to exile and
 * schedules the step-based delayed return under its owner's control, the same primitive
 * Transluminant uses for its delayed token. A token exiled this way simply ceases to exist, per
 * the printed ruling; that falls out of the move rather than needing a special case.
 */
val VoyagerStaff = card("Voyager Staff") {
    manaCost = "{1}"
    typeLine = "Artifact"
    oracleText = "{2}, Sacrifice this artifact: Exile target creature. Return the exiled card to " +
        "the battlefield under its owner's control at the beginning of the next end step."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.SacrificeSelf)
        val t = target("target creature", Targets.Creature)
        effect = Patterns.Exile.exileUntilEndStep(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "274"
        artist = "Tsutomu Kawade"
        flavorText = "Voyager staffs are sought by those hoping to find an escape from the " +
            "sprawling glut of Ravnica."
        imageUri = "https://cards.scryfall.io/normal/front/7/6/76554c65-edea-48b0-b3a5-483dfe80eaac.jpg?1783943594"
        ruling(
            "2024-01-12",
            "If a creature token is exiled by Voyager Staff's ability, it will cease to exist. " +
                "It won't return to the battlefield."
        )
    }
}
