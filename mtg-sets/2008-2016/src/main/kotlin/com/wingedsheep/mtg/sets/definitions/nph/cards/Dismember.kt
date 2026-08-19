package com.wingedsheep.mtg.sets.definitions.nph.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dismember
 * {1}{B/P}{B/P}
 * Instant
 *
 * Target creature gets -5/-5 until end of turn.
 *
 * ({B/P} can be paid with either {B} or 2 life — CR 107.4f.) The Phyrexian mana symbols are
 * parsed directly by `ManaCost.parse` (same syntax as Skrelv, Defector Mite / Namor, the
 * Sub-Mariner's activated-ability costs); a Phyrexian pip always contributes 1 to mana value
 * regardless of how it's paid (2024-06-07 ruling), so no special-casing is needed here.
 */
val Dismember = card("Dismember") {
    manaCost = "{1}{B/P}{B/P}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -5/-5 until end of turn."
    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(-5, -5, t)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "57"
        artist = "Terese Nielsen"
        flavorText = "\"You serve Phyrexia. Your pieces would better serve Phyrexia elsewhere.\"\n" +
            "—Azax-Azog, the Demon Thane"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/064dfdeb-485f-473e-9fa0-8fdb7638cdc6.jpg?1783941314"
        ruling(
            "2024-06-07",
            "A Phyrexian mana symbol contributes 1 toward the mana value of a card, even if life " +
                "is paid for it. Specifically, Dismember's mana value is always 3."
        )
    }
}
