package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Llanowar Stalker
 * {G}
 * Creature — Elf Warrior
 * 1/1
 * Whenever another creature you control enters, this creature gets +1/+0 until end of turn.
 */
val LlanowarStalker = card("Llanowar Stalker") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    oracleText = "Whenever another creature you control enters, this creature gets +1/+0 until end of turn."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "171"
        artist = "Fariba Khamseh"
        flavorText = "If you spot one elf in Llanowar, it's likely there's another with a knife at your back."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2d714d8-0e9b-4761-a0ef-3429b4e2f5b7.jpg?1783921297"
    }
}
