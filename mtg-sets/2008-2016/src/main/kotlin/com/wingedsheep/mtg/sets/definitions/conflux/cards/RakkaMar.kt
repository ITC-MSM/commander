package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rakka Mar
 * {2}{R}{R}
 * Legendary Creature — Human Shaman
 * 2/2
 * Haste
 * {R}, {T}: Create a 3/1 red Elemental creature token with haste.
 *
 * Haste is a printed [Keyword]. The activation is one [Effects.CreateToken] behind a
 * [Costs.Composite] of mana plus [Costs.Tap] — the token's own haste rides on the effect's
 * `keywords` set, which is what makes it able to attack the turn it is minted.
 */
val RakkaMar = card("Rakka Mar") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "Haste\n" +
        "{R}, {T}: Create a 3/1 red Elemental creature token with haste."

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.Tap)
        effect = Effects.CreateToken(
            power = 3,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Elemental"),
            keywords = setOf(Keyword.HASTE)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Jason Chan"
        flavorText = "\"The finest pawns are those with pawns of their own.\" —Nicol Bolas"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d569b01-af52-41a6-9ce4-02ed2e057038.jpg"
    }
}
