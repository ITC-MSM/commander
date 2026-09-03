package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shriekdiver — War of the Spark #103 (canonical printing)
 * {2}{B}
 * Creature — Zombie Bird Warrior
 * 2/1
 * Flying
 * {1}: This creature gains haste until end of turn.
 *
 * "Until end of turn" is [Effects.GrantKeyword]'s default duration, so the grant needs no
 * duration argument; [EffectTarget.Self] is the untargeted "this creature".
 */
val Shriekdiver = card("Shriekdiver") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Bird Warrior"
    oracleText = "Flying\n" +
        "{1}: This creature gains haste until end of turn."
    power = 2
    toughness = 1

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}")
        effect = Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "103"
        artist = "Piotr Dura"
        flavorText = "\"It's faster than we are. Draw its attention while the Ledev take aim!\"\n—Shauntal, Boros legionnaire"
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0a1c3148-7a6f-4963-af0b-18d9a156bf22.jpg"
    }
}
