package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Neurok Invisimancer
 * {1}{U}{U}
 * Creature — Human Wizard
 * 2/1
 *
 * This creature can't be blocked.
 * When this creature enters, target creature can't be blocked this turn.
 *
 * "Can't be blocked" is an [AbilityFlag], not a CR 702 keyword — so the printed evasion is a
 * card-level flag and the enters trigger is the ordinary until-end-of-turn grant over that flag.
 */
val NeurokInvisimancer = card("Neurok Invisimancer") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "This creature can't be blocked.\n" +
        "When this creature enters, target creature can't be blocked this turn."

    flags(AbilityFlag.CANT_BE_BLOCKED)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Izzy"
        flavorText = "\"They won't see your shadow or hear your breath, but they will feel your blade.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e88f78f4-77d8-4c3e-a5bf-a9dd902aaae1.jpg?1783941738"
    }
}
