package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Suspicious Bookcase
 * {2}
 * Artifact Creature — Wall
 * 0 / 4
 * Defender
 * {3}, {T}: Target creature can't be blocked this turn.
 */
val SuspiciousBookcase = card("Suspicious Bookcase") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Wall"
    oracleText = "Defender\n{3}, {T}: Target creature can't be blocked this turn."
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "246"
        artist = "Anastasia Ovchinnikova"
        flavorText = "All the books were dusty with disuse, save the one titled *Camouflage and Its Practical Applications*."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/ccf01421-856e-4cdd-8938-148928626f56.jpg?1783934508"
    }
}
