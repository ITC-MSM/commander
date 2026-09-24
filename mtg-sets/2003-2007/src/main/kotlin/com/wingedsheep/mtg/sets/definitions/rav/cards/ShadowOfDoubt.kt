package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shadow of Doubt — Ravnica: City of Guilds #253
 * {U/B}{U/B} · Instant
 *
 * Players can't search libraries this turn.
 * Draw a card.
 *
 * `CantSearchLibraries` on every player, then a draw. The restriction is read where a search
 * happens — a gather marked `search = true` — so looking at or revealing the top of a library
 * (the card's third ruling) and bulk library moves that aren't searches are untouched, and a
 * blocked "search …, then shuffle" still shuffles (its second ruling).
 */
val ShadowOfDoubt = card("Shadow of Doubt") {
    manaCost = "{U/B}{U/B}"
    colorIdentity = "UB"
    typeLine = "Instant"
    oracleText = "({U/B} can be paid with either {U} or {B}.)\nPlayers can't search libraries this turn.\nDraw a card."

    spell {
        effect = Effects.Composite(
            Effects.CantSearchLibraries(EffectTarget.PlayerRef(Player.Each)),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "253"
        artist = "Greg Staples"
        flavorText = "\"Your ignorance is my bliss.\"\n—Szadek"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7dbd0e3c-b26d-4080-b7cf-1c64fce09668.jpg?1783943602"
    }
}
