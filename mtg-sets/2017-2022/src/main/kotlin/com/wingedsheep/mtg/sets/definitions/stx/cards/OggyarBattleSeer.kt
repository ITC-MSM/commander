package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Oggyar Battle-Seer — Strixhaven: School of Mages #209 (canonical printing)
 * {3}{U}{R} · Creature — Ogre Shaman · 3/4
 *
 * Haste
 * {T}: Scry 1.
 *
 * Haste is a plain [Keyword] marker; the tap ability is [Costs.Tap] paying for a bare
 * [Effects.Scry].
 */
val OggyarBattleSeer = card("Oggyar Battle-Seer") {
    manaCost = "{3}{U}{R}"
    colorIdentity = "RU"
    typeLine = "Creature — Ogre Shaman"
    oracleText =
        "Haste\n" +
        "{T}: Scry 1."
    power = 3
    toughness = 4

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.Scry(1)
        description = "{T}: Scry 1."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "209"
        artist = "Karl Kopinski"
        flavorText = "\"May Ganathog bless us with bloody visions of glory restored!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a38329f1-af6e-47b8-86e4-f2a39e1edbf8.jpg?1783927304"
    }
}
