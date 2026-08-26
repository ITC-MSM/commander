package com.wingedsheep.mtg.sets.definitions.vis.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * River Boa
 * {1}{G}
 * Creature — Snake
 * 2/1
 * Islandwalk (This creature can't be blocked as long as defending player controls an Island.)
 * {G}: Regenerate this creature.
 *
 * Canonical printing: Visions (1997) is River Boa's earliest real-expansion printing.
 */
val RiverBoa = card("River Boa") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    power = 2
    toughness = 1
    oracleText = "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)\n" +
        "{G}: Regenerate this creature."

    keywords(Keyword.ISLANDWALK)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Steve White"
        flavorText = "\"But no one heard the snake's gentle hiss for peace over the elephant's trumpeting of war.\"\n—Afari, *Tales*"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e9d5aaf-b7e8-4676-aec8-7d29a0169a2c.jpg"
    }
}
