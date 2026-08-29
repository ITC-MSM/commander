package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Drowner of Secrets
 * {2}{U}
 * Creature — Merfolk Wizard
 * 1/3
 * Tap an untapped Merfolk you control: Target player mills a card.
 *
 * The cost is [Costs.TapPermanents], not the `{T}` symbol: "you control" and "untapped" are
 * carried by the atom itself, so the filter only has to say *Merfolk permanent*. Drowner is
 * itself a legal choice (no `excludeSelf`), and because tapping this way is a cost rather than
 * `{T}`, summoning sickness never applies (CR 302.6).
 */
val DrownerOfSecrets = card("Drowner of Secrets") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 3
    oracleText = "Tap an untapped Merfolk you control: Target player mills a card."

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 1,
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK)
        )
        val player = target("target player", Targets.Player)
        effect = Patterns.Library.mill(1, player)
        description = "Tap an untapped Merfolk you control: Target player mills a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Rebecca Guay"
        flavorText = "Merrows consider themselves the keepers of Lorwyn's past—and consider it their duty to edit when necessary."
        imageUri = "https://cards.scryfall.io/normal/front/1/4/149aa77e-fc10-4b9b-8f38-cc6db5be7b79.jpg?1783942904"
    }
}
