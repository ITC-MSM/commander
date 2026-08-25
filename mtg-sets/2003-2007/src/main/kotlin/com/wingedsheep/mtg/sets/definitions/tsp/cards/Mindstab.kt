package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Mindstab
 * {5}{B}
 * Sorcery
 * Target player discards three cards.
 * Suspend 4—{B} (Rather than cast this card from your hand, you may pay {B} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)
 *
 * [Effects.Discard] is the facade over the Gather → Select → Move (Discard) pipeline; the
 * bound target both scopes the hand that is gathered and becomes the chooser, so the discarding
 * player picks their own three cards. Suspend is the parameterized [KeywordAbility.Suspend].
 */
val Mindstab = card("Mindstab") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target player discards three cards.\n" +
        "Suspend 4—{B} (Rather than cast this card from your hand, you may pay {B} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)"

    spell {
        val t = target("target", Targets.Player)
        effect = Effects.Discard(3, t)
    }

    keywordAbility(KeywordAbility.suspend("{B}", 4))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "119"
        artist = "Mark Tedin"
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3efd28ef-77db-4e7b-a69d-5a089e016737.jpg"
    }
}
