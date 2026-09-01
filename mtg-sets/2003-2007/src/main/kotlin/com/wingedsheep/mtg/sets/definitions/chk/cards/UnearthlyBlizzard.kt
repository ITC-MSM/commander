package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.IterationSpace
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Unearthly Blizzard
 * {2}{R}
 * Sorcery — Arcane
 *
 * Up to three target creatures can't block this turn.
 *
 * "Up to three target creatures" is one requirement with `count = 3, optional = true`, and the body
 * runs once per chosen target through [IterationSpace.Targets] — so zero, one, two or three targets
 * all resolve, and a target that became illegal is simply skipped.
 */
val UnearthlyBlizzard = card("Unearthly Blizzard") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery — Arcane"
    oracleText = "Up to three target creatures can't block this turn."

    spell {
        target = TargetCreature(count = 3, optional = true)
        effect = ForEachEffect(
            space = IterationSpace.Targets,
            body = Effects.CantBlock()
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "196"
        artist = "Joel Thomas"
        flavorText = "\"We are trapped. The mountains and blinding kami storms have made us " +
            "hopelessly lost. We are starving. In the name of all things sacred, please, send " +
            "help . . . .\"\n—Lost Battalion, final message to General Takeno"
        imageUri = "https://cards.scryfall.io/normal/front/d/0/d034ad87-fd28-4c23-b897-1d6343ce8282.jpg?1783944294"
    }
}
