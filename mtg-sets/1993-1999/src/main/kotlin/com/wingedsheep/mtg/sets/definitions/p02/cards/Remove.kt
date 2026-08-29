package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.conditions.YouWereAttackedThisStep

/**
 * Remove
 * {U}
 * Instant
 * Cast this spell only during the declare attackers step and only if you've been attacked this step.
 * Return target attacking creature to its owner's hand.
 *
 * The blue half of the Portal "combat trick" timing pair — [com.wingedsheep.sdk.dsl.SpellBuilder.castOnlyDuring]
 * plus `castOnlyIf(YouWereAttackedThisStep)`, the same restriction pair Rally the Troops carries.
 */
val Remove = card("Remove") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText =
        "Cast this spell only during the declare attackers step and only if you've been attacked this step.\n" +
        "Return target attacking creature to its owner's hand."

    spell {
        castOnlyDuring(Step.DECLARE_ATTACKERS)
        castOnlyIf(YouWereAttackedThisStep)
        val attacker = target("target", Targets.AttackingCreature)
        effect = Effects.ReturnToHand(attacker)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "DiTerlizzi"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/adb08549-8ff8-410f-b817-0255abaf8b1e.jpg"
    }
}
