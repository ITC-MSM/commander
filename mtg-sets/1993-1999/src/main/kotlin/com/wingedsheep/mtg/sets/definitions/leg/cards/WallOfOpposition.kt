package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wall of Opposition
 * {3}{R}{R}
 * Creature — Wall
 * 0/6
 *
 * Defender (This creature can't attack.)
 * {1}: This creature gets +1/+0 until end of turn.
 */
val WallOfOpposition = card("Wall of Opposition") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 6
    oracleText = "Defender (This creature can't attack.)\n{1}: This creature gets +1/+0 until end of turn."

    keywords(Keyword.DEFENDER)
    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "171"
        artist = "Harold McNeill"
        flavorText = "Like so many obstacles in life, the Wall of Opposition is but an illusion, held fast by the " +
            "focus and belief of the one who creates it."
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b3d1430-9978-4983-a4fd-d1fa8dea2169.jpg?1783948053"
    }
}
