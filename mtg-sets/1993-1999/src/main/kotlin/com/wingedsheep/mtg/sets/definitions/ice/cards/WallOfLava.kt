package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wall of Lava
 * {1}{R}{R}
 * Creature — Wall
 * 1/3
 *
 * Defender (This creature can't attack.)
 * {R}: This creature gets +1/+1 until end of turn.
 *
 * Defender plus firebreathing: the pump is `Effects.ModifyStats` onto `EffectTarget.Self` at the
 * facade's default `Duration.EndOfTurn`.
 */
val WallOfLava = card("Wall of Lava") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wall"
    power = 1
    toughness = 3
    oracleText = "Defender (This creature can't attack.)\n" +
        "{R}: This creature gets +1/+1 until end of turn."

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "223"
        artist = "Pete Venters"
        flavorText = "\"Now *there's* something you don't see every day.\"\n—Jaya Ballard, Task Mage"
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b99d6d11-b3f7-4d73-967c-3049af82a9d8.jpg"
    }
}
