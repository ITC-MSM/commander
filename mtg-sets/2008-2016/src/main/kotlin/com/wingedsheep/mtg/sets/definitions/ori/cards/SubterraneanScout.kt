package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Subterranean Scout
 * {1}{R}
 * Creature — Goblin Scout
 * 2/1
 * When this creature enters, target creature with power 2 or less can't be blocked this turn.
 */
val SubterraneanScout = card("Subterranean Scout") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Scout"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, target creature with power 2 or less can't be blocked this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target creature with power 2 or less", Targets.CreatureWithPowerAtMost(2))
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, t)
        description = "When this creature enters, target creature with power 2 or less can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Lucas Graciano"
        flavorText = "When things get ugly above ground, goblins resort to alternate routes of passage."
        imageUri = "https://cards.scryfall.io/normal/front/3/c/3c9289dd-f1a3-4be5-8ed1-4b4dd4e97743.jpg?1783938325"
    }
}
