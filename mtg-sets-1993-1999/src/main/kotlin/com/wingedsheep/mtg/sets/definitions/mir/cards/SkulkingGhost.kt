package com.wingedsheep.mtg.sets.definitions.mir.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skulking Ghost
 * {1}{B}
 * Creature — Spirit
 * 2/1
 * Flying
 * When this creature becomes the target of a spell or ability, sacrifice it.
 */
val SkulkingGhost = card("Skulking Ghost") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 1
    oracleText = "Flying\nWhen this creature becomes the target of a spell or ability, sacrifice it."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.BecomesTarget
        effect = Effects.SacrificeTarget(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "143"
        artist = "Robert Bliss"
        flavorText = "\"They exist only so long as the living take no notice.\"\n—Kifimbo, Shadow Guildmage"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8ca7e96-0545-4f36-85c0-944d5c0b760a.jpg?1562722860"
    }
}
