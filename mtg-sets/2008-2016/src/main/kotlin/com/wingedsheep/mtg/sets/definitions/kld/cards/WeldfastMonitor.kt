package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Weldfast Monitor
 * {3}
 * Artifact Creature — Lizard
 * 3 / 2
 *
 * {R}: This creature gains menace until end of turn.
 *
 * The grant is until end of turn by default, so no duration is spelled out.
 */
val WeldfastMonitor = card("Weldfast Monitor") {
    manaCost = "{3}"
    colorIdentity = "R"
    typeLine = "Artifact Creature — Lizard"
    oracleText = "{R}: This creature gains menace until end of turn."
    power = 3
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.GrantKeyword(Keyword.MENACE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "238"
        artist = "Jakub Kasper"
        flavorText = "The avant-garde among Ghirapur's inventors disregard the Consulate's safety standards completely."
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75720c1b-8b04-4e45-ab47-018c04576e83.jpg?1783937146"
    }
}
