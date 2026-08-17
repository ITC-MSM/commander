package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.collectEvidence
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Behind the Mask — Murders at Karlov Manor #39
 *
 * The optional collect-evidence cast cost stamps the shared evidence choice, which selects the
 * alternate 1/1 base stats at resolution. [Effects.BecomeCreature] adds both CREATURE and ARTIFACT
 * while retaining the target's other types, and its Layer-7b base-stat change composes correctly
 * with counters and later power/toughness modifiers.
 */
val BehindTheMask = card("Behind the Mask") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, you may collect evidence 6. " +
        "(Exile cards with total mana value 6 or greater from your graveyard.)\n" +
        "Until end of turn, target artifact or creature becomes an artifact creature with base " +
        "power and toughness 4/3. If evidence was collected, it has base power and toughness 1/1 " +
        "until end of turn instead."

    collectEvidence(6)

    spell {
        val permanent = target(
            "target artifact or creature",
            TargetPermanent(filter = TargetFilter.CreatureOrArtifact),
        )
        effect = ConditionalEffect(
            condition = Conditions.WasEvidenceCollected,
            effect = Effects.BecomeCreature(
                target = permanent,
                power = 1,
                toughness = 1,
                addTypes = setOf("ARTIFACT"),
            ),
            elseEffect = Effects.BecomeCreature(
                target = permanent,
                power = 4,
                toughness = 3,
                addTypes = setOf("ARTIFACT"),
            ),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Caio Monteiro"
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e522b043-fbd8-48a4-9f20-39e2a66a35ec.jpg?1783912917"

        ruling(
            "2024-02-02",
            "Behind the Mask overwrites earlier effects that set specific power and toughness " +
                "values, while modifiers and counters continue to apply.",
        )
        ruling(
            "2024-02-02",
            "Making a Vehicle an artifact creature this way does not count as crewing it.",
        )
    }
}
