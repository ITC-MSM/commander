package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Runaway Carriage (Shadows over Innistrad #261)
 * {4}
 * Artifact Creature — Construct
 * 5 / 6
 *
 * Trample
 * When this creature attacks or blocks, sacrifice it at end of combat.
 *
 * One printed sentence, two triggered abilities — the same spelling Mardu Blazebringer uses. A
 * creature can never both attack and block in one combat, so the disjunction fires at most once
 * either way. The sacrifice is [SacrificeSelfEffect], the verb with no object that reads the
 * delayed trigger's own source.
 */
val RunawayCarriage = card("Runaway Carriage") {
    manaCost = "{4}"
    typeLine = "Artifact Creature — Construct"
    power = 5
    toughness = 6
    oracleText = "Trample\n" +
        "When this creature attacks or blocks, sacrifice it at end of combat."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = CreateDelayedTriggerEffect(
            step = Step.END_COMBAT,
            effect = SacrificeSelfEffect
        )
    }

    triggeredAbility {
        trigger = Triggers.Blocks
        effect = CreateDelayedTriggerEffect(
            step = Step.END_COMBAT,
            effect = SacrificeSelfEffect
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "261"
        artist = "Kev Walker"
        flavorText = "\"They left in one piece. I can't speak to how they arrived.\"\n—Rupirk, porter at the Rusted Anchor Inn"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c8ce5d3-0184-4cfb-a41d-3d58229b2a5f.jpg?1783937706"
    }
}
