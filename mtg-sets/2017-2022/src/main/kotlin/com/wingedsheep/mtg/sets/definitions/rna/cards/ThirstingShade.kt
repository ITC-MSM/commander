package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thirsting Shade — Ravnica Allegiance #87
 * {B} · Creature — Shade · 1 / 1
 *
 * The classic Shade firebreathing shape: a repeatable mana-only pump on itself. No {T} in
 * the cost, so it can be activated any number of times and while attacking.
 */
val ThirstingShade = card("Thirsting Shade") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    power = 1
    toughness = 1
    oracleText = "Lifelink\n" +
        "{2}{B}: This creature gets +1/+1 until end of turn."

    keywords(Keyword.LIFELINK)
    activatedAbility {
        cost = Costs.Mana("{2}{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Seb McKinnon"
        flavorText = "\"Your life is a blinding light, your breath a gale, your pulse a deafening drum. Be still. Be still.\"\n" +
        "—Dahlya Trul, \"Irbitov Lament\""
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a920c2e6-4a1f-487c-ad3f-b772443f0633.jpg"
    }
}
