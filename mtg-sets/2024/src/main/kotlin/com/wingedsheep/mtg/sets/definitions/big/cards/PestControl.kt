package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Pest Control
 * {W}{B}
 * Sorcery
 *
 * Destroy all nonland permanents with mana value 1 or less.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * A filtered board wipe: every nonland permanent whose mana value is 1 or less is
 * destroyed. [Effects.DestroyAll] runs the projected-state-aware destroy pipeline, so
 * the mana-value filter is evaluated against the current battlefield projection.
 */
val PestControl = card("Pest Control") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Sorcery"
    oracleText = "Destroy all nonland permanents with mana value 1 or less.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        effect = Effects.DestroyAll(
            filter = GameObjectFilter.NonlandPermanent.manaValueAtMost(1),
        )
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "22"
        artist = "Jonas De Ro"
        flavorText = "\"How dare these varmints befoul the shining streets of Prosperity?\"\n" +
            "—Baron Bertram Graywater"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4a01b92-dafb-4ea6-8eff-29f881f6be24.jpg?1739804223"
    }
}
