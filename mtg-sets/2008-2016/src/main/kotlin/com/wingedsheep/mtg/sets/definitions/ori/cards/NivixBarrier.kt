package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Nivix Barrier
 * {3}{U}
 * Creature — Illusion Wall
 * 0/4
 *
 * Flash (You may cast this spell any time you could cast an instant.)
 * Defender (This creature can't attack.)
 * When this creature enters, target attacking creature gets -4/-0 until end of turn.
 */
val NivixBarrier = card("Nivix Barrier") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Illusion Wall"
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)\n" +
        "Defender (This creature can't attack.)\n" +
        "When this creature enters, target attacking creature gets -4/-0 until end of turn."
    power = 0
    toughness = 4

    keywords(Keyword.FLASH, Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target attacking creature", TargetCreature(filter = TargetFilter.Creature.attacking()))
        effect = Effects.ModifyStats(-4, 0, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Mathias Kollros"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b9b968d-b0cc-411d-9366-8358be28aef2.jpg"
    }
}
