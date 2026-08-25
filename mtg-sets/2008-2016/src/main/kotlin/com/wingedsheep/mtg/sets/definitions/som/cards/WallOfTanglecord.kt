package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wall of Tanglecord — Scars of Mirrodin #222
 * {2} · Artifact Creature — Wall · 0 / 6
 *
 * Defender
 * {G}: This creature gains reach until end of turn. (It can block creatures with flying.)
 *
 * A colorless artifact with a green activation cost — hence the green color identity on an
 * otherwise colorless card (CR 903.4).
 */
val WallOfTanglecord = card("Wall of Tanglecord") {
    manaCost = "{2}"
    colorIdentity = "G"
    typeLine = "Artifact Creature — Wall"
    power = 0
    toughness = 6
    oracleText = "Defender\n" +
        "{G}: This creature gains reach until end of turn. (It can block creatures with flying.)"

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.GrantKeyword(Keyword.REACH, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "222"
        artist = "Vance Kovacs"
        flavorText = "Rootlike fibers travel far from Mirrodin's metallic forests, emerging from the crust to drink in the mana-infused sunlight."
        imageUri = "https://cards.scryfall.io/normal/front/7/9/792e2aed-ce6e-4fa1-a31c-a4574e5cf1f5.jpg?1783941691"
    }
}
