package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Minotaur Sureshot
 * {2}{R}
 * Creature — Minotaur Archer
 * 2/3
 *
 * Reach (This creature can block creatures with flying.)
 * {1}{R}: This creature gets +1/+0 until end of turn.
 */
val MinotaurSureshot = card("Minotaur Sureshot") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Archer"
    oracleText = "Reach (This creature can block creatures with flying.)\n{1}{R}: This creature gets +1/+0 until end of turn."
    power = 2
    toughness = 3

    keywords(Keyword.REACH)

    activatedAbility {
        cost = Costs.Mana("{1}{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
        description = "{1}{R}: This creature gets +1/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Joseph Meehan"
        flavorText = "\"Those wings are no advantage. I will pin them to the ceiling of the Hekma.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/7/777b98c3-d0b8-4ee3-9d89-e86344269ff1.jpg?1783936484"
    }
}
