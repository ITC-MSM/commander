package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bloodbraid Elf
 * {2}{R}{G}
 * Creature — Elf Berserker
 * 3/2
 * Haste (This creature can attack and {T} as soon as it comes under your control.)
 * Cascade (When you cast this spell, exile cards from the top of your library until you exile a nonland card that costs less. You may cast it without paying its mana cost. Put the exiled cards on the bottom in a random order.)
 *
 * Haste is a plain keyword, but [Keyword.CASCADE] is display-only — nothing in the rules engine
 * reads it. Cascade is itself a "when you cast this spell" triggered ability (CR 702.85a), so the
 * behavior lives in the [Triggers.WhenYouCastThisSpell] trigger feeding [Effects.Cascade], with the
 * keyword kept only for the printed line — the same shape as `cmr/cards/AnnoyedAltisaur.kt`.
 */
val BloodbraidElf = card("Bloodbraid Elf") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Elf Berserker"
    power = 3
    toughness = 2
    oracleText = "Haste (This creature can attack and {T} as soon as it comes under your control.)\n" +
        "Cascade (When you cast this spell, exile cards from the top of your library until you " +
        "exile a nonland card that costs less. You may cast it without paying its mana cost. Put " +
        "the exiled cards on the bottom in a random order.)"

    keywords(Keyword.HASTE, Keyword.CASCADE)

    // Cascade — the cast trigger the keyword abbreviates.
    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.Cascade
        description = "Cascade"
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "50"
        artist = "Dominick Domingo"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ef67487-c8e5-49bb-b0f7-e073ff2e31f1.jpg?1783942431"
    }
}
