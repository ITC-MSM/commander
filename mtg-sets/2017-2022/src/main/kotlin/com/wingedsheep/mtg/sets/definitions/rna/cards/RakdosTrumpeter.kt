package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rakdos Trumpeter — Ravnica Allegiance #84
 * {1}{B} · Creature — Human Shaman · 1 / 3
 *
 * Menace is printed with reminder text; the activated ability is the same off-colour pump
 * as the rest of the RNA "splash" commons.
 */
val RakdosTrumpeter = card("Rakdos Trumpeter") {
    manaCost = "{1}{B}"
    colorIdentity = "BR"
    typeLine = "Creature — Human Shaman"
    power = 1
    toughness = 3
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "{3}{R}: This creature gets +2/+0 until end of turn."

    keywords(Keyword.MENACE)
    activatedAbility {
        cost = Costs.Mana("{3}{R}")
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Eric Deschamps"
        flavorText = "\"The louder their performance, the quieter we become in comparison. They are the perfect distractions, for only fools ignore the Rakdos.\"\n" +
        "—Lazav"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/2822aff3-9985-424b-9f19-b49e987c25e4.jpg"
    }
}
