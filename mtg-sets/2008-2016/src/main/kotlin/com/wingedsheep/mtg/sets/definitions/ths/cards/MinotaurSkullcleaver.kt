package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Minotaur Skullcleaver
 * {2}{R}
 * Creature — Minotaur Berserker
 * 2/2
 *
 * Haste
 * When this creature enters, it gets +2/+0 until end of turn.
 */
val MinotaurSkullcleaver = card("Minotaur Skullcleaver") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Minotaur Berserker"
    oracleText = "Haste\nWhen this creature enters, it gets +2/+0 until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
        description = "When this creature enters, it gets +2/+0 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "130"
        artist = "Phill Simmer"
        flavorText = "\"Their only dreams are of full stomachs.\"\n—Kleon the Iron-Booted"
        imageUri = "https://cards.scryfall.io/normal/front/6/8/6854c913-d4bd-42f7-901c-f3c25be5c4b2.jpg?1783939759"
    }
}
