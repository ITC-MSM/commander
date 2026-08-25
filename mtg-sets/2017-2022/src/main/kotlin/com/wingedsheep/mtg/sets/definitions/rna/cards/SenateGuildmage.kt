package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Senate Guildmage — Ravnica Allegiance #204
 * {W}{U} · Creature — Human Wizard · 2 / 2
 *
 * The Azorius entry in the RNA Guildmage cycle. "Draw a card, then discard a card" is one
 * ordered composite, not two abilities — the discard is chosen after the draw resolves.
 */
val SenateGuildmage = card("Senate Guildmage") {
    manaCost = "{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "{W}, {T}: You gain 2 life.\n" +
        "{U}, {T}: Draw a card, then discard a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        effect = Effects.GainLife(2)
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        effect = Effects.Composite(listOf(
            Effects.DrawCards(1),
            Patterns.Hand.discardCards(1)
        ))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "204"
        artist = "G-host Lee"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/913b3a2a-e3fd-4095-ab2a-5e356ea179df.jpg"
    }
}
