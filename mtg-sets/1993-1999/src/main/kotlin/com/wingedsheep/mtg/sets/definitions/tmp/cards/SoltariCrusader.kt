package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Soltari Crusader
 * {2}{W}
 * Creature — Soltari Knight
 * 2/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * {1}{W}: This creature gets +1/+0 until end of turn.
 */
val SoltariCrusader = card("Soltari Crusader") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Soltari Knight"
    power = 2
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "{1}{W}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.SHADOW)

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "41"
        artist = "Randy Gallegos"
        flavorText = "\"Carry war to the Dauthi, no matter the way, no matter the world.\"\n" +
            "—Soltari battle chant"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6cd07471-b216-465c-9946-1eac689db32e.jpg"
    }
}
