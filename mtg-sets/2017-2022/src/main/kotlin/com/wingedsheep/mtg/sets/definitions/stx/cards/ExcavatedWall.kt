package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Excavated Wall — Strixhaven: School of Mages #255 (canonical printing)
 * {1} · Artifact Creature — Wall · 0/4
 *
 * Defender
 * {1}, {T}: Mill a card. (Put the top card of your library into your graveyard.)
 *
 * A colourless Wall with a repeatable self-mill. Defender is the bare keyword; the activation is
 * [Patterns.Library.mill] — the Gather → Move pipeline whose top-of-library source is flagged as a
 * mill so "whenever you mill" watchers see it.
 */
val ExcavatedWall = card("Excavated Wall") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Wall"
    oracleText =
        "Defender\n" +
        "{1}, {T}: Mill a card. (Put the top card of your library into your graveyard.)"
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER)

    // {1}, {T}: Mill a card.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Patterns.Library.mill(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "255"
        artist = "Zezhou Chen"
        flavorText = "\"Don't be fooled by the quiet. These old stones are trying to talk to you.\"\n—Augusta, Lorehold dean"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fed54d00-b11f-4529-864c-63a114617b36.jpg?1783927281"
    }
}
