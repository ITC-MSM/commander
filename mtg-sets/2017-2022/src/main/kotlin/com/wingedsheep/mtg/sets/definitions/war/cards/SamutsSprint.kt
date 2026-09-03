package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Samut's Sprint
 * {R}
 * Instant
 * Target creature gets +2/+1 and gains haste until end of turn. Scry 1.
 *
 * Sentence-shaped nesting: the first sentence is one composite (pump plus keyword grant over the
 * same bound target, both defaulting to `Duration.EndOfTurn`), the scry is the second sentence.
 */
val SamutsSprint = card("Samut's Sprint") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+1 and gains haste until end of turn. Scry 1."

    spell {
        val t = target("target", TargetCreature())
        effect = Effects.Composite(
            Effects.Composite(
                Effects.ModifyStats(2, 1, t),
                Effects.GrantKeyword(Keyword.HASTE, t)
            ),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Aleksi Briclot"
        flavorText = "Samut's war was intensely personal. Every hour she faced enemies she once loved as friends and horrors she once revered as gods."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/94bda699-2b5d-4f5b-bee5-792b99d2b64a.jpg"
    }
}
