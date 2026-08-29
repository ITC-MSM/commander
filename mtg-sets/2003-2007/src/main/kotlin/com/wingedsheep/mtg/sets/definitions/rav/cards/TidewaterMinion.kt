package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tidewater Minion
 * {3}{U}{U}
 * Creature — Elemental Minion
 * 4/4
 * Defender (This creature can't attack.)
 * {4}: This creature loses defender until end of turn.
 * {T}: Untap target permanent.
 */
val TidewaterMinion = card("Tidewater Minion") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Minion"
    oracleText = "Defender (This creature can't attack.)\n" +
        "{4}: This creature loses defender until end of turn.\n" +
        "{T}: Untap target permanent."
    power = 4
    toughness = 4

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Mana("{4}")
        effect = Effects.RemoveKeyword(Keyword.DEFENDER, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Tap
        val t = target("target permanent", Targets.Permanent)
        effect = Effects.Untap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Tomas Giorello"
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67ffa68f-cc21-41c8-ad3d-d6b60a4ccd36.jpg"
    }
}
