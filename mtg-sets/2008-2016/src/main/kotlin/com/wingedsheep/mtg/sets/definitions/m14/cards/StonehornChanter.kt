package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stonehorn Chanter
 * {5}{W}
 * Creature — Rhino Cleric
 * 4 / 4
 * {5}{W}: This creature gains vigilance and lifelink until end of turn.
 */
val StonehornChanter = card("Stonehorn Chanter") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Rhino Cleric"
    power = 4
    toughness = 4
    oracleText = "{5}{W}: This creature gains vigilance and lifelink until end of turn. (Attacking doesn't cause it to tap. Damage dealt by it also causes you to gain that much life.)"

    activatedAbility {
        cost = Costs.Mana("{5}{W}")
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self)
        )
        description = "{5}{W}: This creature gains vigilance and lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "39"
        artist = "Raymond Swanland"
        flavorText = "With the Stonehorn, piety and power are one."
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd6ec61b-c039-4526-a359-a7947eeba5c3.jpg"
    }
}
