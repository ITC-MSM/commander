package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sunmane Pegasus
 * {3}{W}
 * Creature — Pegasus
 * 2/3
 *
 * Flying
 * {1}{W}: This creature gains vigilance and lifelink until end of turn. (Attacking doesn't cause it
 * to tap. Damage dealt by it also causes you to gain that much life.)
 *
 * "Gains vigilance and lifelink" is two independent [Effects.GrantKeyword] grants on
 * [EffectTarget.Self] composed in order, not a single multi-keyword grant — each keyword is its own
 * until-end-of-turn floating effect, so one can be stripped without the other.
 */
val SunmanePegasus = card("Sunmane Pegasus") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Pegasus"
    power = 2
    toughness = 3
    oracleText = "Flying\n" +
        "{1}{W}: This creature gains vigilance and lifelink until end of turn. " +
        "(Attacking doesn't cause it to tap. Damage dealt by it also causes you to gain that much life.)"

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "John Severin Brassell"
        flavorText = "Chosen by Heliod, Daxos approached the pegasus without fear, and rode it without " +
            "saddle or reins."
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52e28f5a-55ee-4fcc-bc16-e59944592fcd.jpg"
    }
}
