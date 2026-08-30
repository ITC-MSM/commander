package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Void Squall
 * {4}{U}
 * Sorcery
 *
 * Return target nonland permanent to its owner's hand.
 * Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)
 *
 * A plain bounce: [Effects.Move] to [Zone.HAND] already sends a permanent to its *owner's* hand,
 * so no controller override is spelled. No `fromZone` guard either — the target is a permanent on
 * the battlefield, which is where the requirement already looks. Rebound is the bare
 * [Keyword.REBOUND], so the same spell bounces a second permanent on your next upkeep.
 */
val VoidSquall = card("Void Squall") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return target nonland permanent to its owner's hand.\n" +
        "Rebound (If you cast this spell from your hand, exile it as it resolves. At the beginning of your next upkeep, you may cast this card from exile without paying its mana cost.)"

    keywords(Keyword.REBOUND)

    spell {
        val t = target("target", TargetObject(filter = TargetFilter.NonlandPermanent))
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d8a7aee-6403-44ce-a119-66147aa311fd.jpg?1783938601"
    }
}
