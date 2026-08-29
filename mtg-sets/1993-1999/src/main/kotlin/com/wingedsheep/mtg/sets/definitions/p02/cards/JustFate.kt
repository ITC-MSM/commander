package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.YouWereAttackedThisStep

/**
 * Just Fate
 * {2}{W}
 * Instant
 * Cast this spell only during the declare attackers step and only if you've been attacked this step.
 * Destroy target attacking creature.
 *
 * The Portal "combat trick" timing pair: [com.wingedsheep.sdk.dsl.SpellBuilder.castOnlyDuring] plus
 * `castOnlyIf(YouWereAttackedThisStep)` — same shape as Rally the Troops in this set.
 */
val JustFate = card("Just Fate") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText =
        "Cast this spell only during the declare attackers step and only if you've been attacked this step.\n" +
        "Destroy target attacking creature."

    spell {
        castOnlyDuring(Step.DECLARE_ATTACKERS)
        castOnlyIf(YouWereAttackedThisStep)
        val victim = target("target", Targets.AttackingCreature)
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "17"
        artist = "Bradley Williams"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6e5e572-030d-4a41-89e6-e720b49bc131.jpg"
    }
}
