package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Soltari Emissary
 * {1}{W}
 * Creature — Soltari Soldier
 * 2/1
 * {W}: This creature gains shadow until end of turn. (It can block or be blocked by only creatures with shadow.)
 */
val SoltariEmissary = card("Soltari Emissary") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Soltari Soldier"
    power = 2
    toughness = 1
    oracleText = "{W}: This creature gains shadow until end of turn. (It can block or be blocked by only creatures with shadow.)"

    activatedAbility {
        cost = Costs.Mana("{W}")
        effect = Effects.GrantKeyword(Keyword.SHADOW, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "Adam Rex"
        flavorText = "Alone at the portal, Ertai began his meditation. He realized immediately that he was not alone."
        imageUri = "https://cards.scryfall.io/normal/front/a/1/a18751d3-052b-4ae5-ba07-16f00a1af40e.jpg"
    }
}
