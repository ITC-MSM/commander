package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Sorceress's Schemes
 * {3}{R}
 * Sorcery
 * Return target instant or sorcery card from your graveyard or exiled card with flashback you own
 * to your hand. Add {R}.
 * Flashback {4}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 *
 * The target is a cross-zone union (CR 115.1 single target, two zone clauses): an instant or sorcery
 * card in your graveyard, OR a card with flashback in your exile that you own — built with
 * [TargetFilter.or]. "Card with flashback" is just the FLASHBACK keyword, which lives in the card's
 * base keywords (derived from its flashback ability) and so matches even on a card in exile.
 */
val SorceressSchemes = card("Sorceress's Schemes") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Return target instant or sorcery card from your graveyard or exiled card with flashback you own to your hand. Add {R}.\n" +
        "Flashback {4}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        // Clause A: instant or sorcery card in your graveyard.
        // Clause B: a card you own with flashback in exile.
        val union = TargetFilter.InstantOrSorceryInGraveyard.ownedByYou()
            .or(TargetFilter(GameObjectFilter.Any).withKeyword(Keyword.FLASHBACK).ownedByYou().inZone(Zone.EXILE))
        val t = target(
            "instant or sorcery card from your graveyard or exiled card with flashback you own",
            TargetObject(filter = union)
        )
        effect = Effects.Composite(
            Effects.ReturnToHand(t),
            Effects.AddMana(Color.RED)
        )
    }

    keywordAbility(KeywordAbility.flashback("{4}{R}"))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "159"
        artist = "Jessica Fong"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7efd7627-0754-4685-9d04-8f5f82f45632.jpg?1748706360"
    }
}
