package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Final Days
 * {2}{B}{B}
 * Sorcery
 * Create two tapped 2/2 black Horror creature tokens. If this spell was cast from a graveyard,
 * instead create X of those tokens, where X is the number of creature cards in your graveyard.
 * Flashback {4}{B}{B}
 *
 * The "instead" is a single token count chosen at resolution: [DynamicAmount.Conditional] gated on
 * [Conditions.WasCastFromGraveyard] yields the graveyard creature count (true, the flashback cast)
 * or a flat 2 (false, a normal cast). Counting happens while this sorcery is still on the stack, so
 * it never counts itself.
 */
val TheFinalDays = card("The Final Days") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Create two tapped 2/2 black Horror creature tokens. If this spell was cast from a " +
        "graveyard, instead create X of those tokens, where X is the number of creature cards in " +
        "your graveyard.\n" +
        "Flashback {4}{B}{B} (You may cast this card from your graveyard for its flashback cost. " +
        "Then exile it.)"

    spell {
        effect = Effects.CreateToken(
            count = DynamicAmount.Conditional(
                condition = Conditions.WasCastFromGraveyard,
                ifTrue = DynamicAmounts.creatureCardsInYourGraveyard(),
                ifFalse = DynamicAmount.Fixed(2),
            ),
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Horror"),
            tapped = true,
            imageUri = "https://cards.scryfall.io/normal/front/d/d/ddcf50c2-24f5-46b4-bfe9-c636bb51bae5.jpg?1748704070",
        )
    }

    keywordAbility(KeywordAbility.flashback("{4}{B}{B}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "101"
        artist = "Fang Xinyu"
        imageUri = "https://cards.scryfall.io/normal/front/b/b/bbf01770-6d0f-4015-b4b4-a74a53cb767a.jpg?1748706140"
    }
}
