package com.wingedsheep.mtg.sets.definitions.znr.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Chilling Trap
 * {U}
 * Instant
 * Target creature gets -4/-0 until end of turn. If you control a Wizard, draw a card.
 *
 * The Wizard clause is a rider on resolution, not a cast condition: it is checked as the spell
 * resolves ([ConditionalEffect]), and per the Scryfall ruling a fizzled spell (illegal target)
 * never gets that far, so no card is drawn. "A Wizard" is a bare tribal noun — any *permanent*
 * with the subtype counts ([Conditions.ControlPermanentOfType]).
 */
val ChillingTrap = card("Chilling Trap") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Target creature gets -4/-0 until end of turn. If you control a Wizard, draw a card."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(-4, 0, creature)
            .then(
                ConditionalEffect(
                    condition = Conditions.ControlPermanentOfType(Subtype("Wizard")),
                    effect = Effects.DrawCards(1),
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "Johann Bodin"
        flavorText = "\"Serves you right for rushing ahead.\" —Kaliea, Sea Gate trapfinder"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/60f9adfd-a940-4d62-894b-84f17c693a10.jpg?1783929401"
        ruling("2020-09-25", "If the target creature is an illegal target by the time Chilling Trap tries to resolve, the spell doesn't resolve. You don't draw a card if you control a Wizard.")
    }
}
