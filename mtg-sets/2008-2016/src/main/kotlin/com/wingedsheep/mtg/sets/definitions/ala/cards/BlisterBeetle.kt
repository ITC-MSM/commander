package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Blister Beetle
 * {1}{B}
 * Creature — Insect
 * 1 / 1
 * When this creature enters, target creature gets -1/-1 until end of turn.
 *
 * A plain [Triggers.EntersBattlefield] (SELF binding) with one named target. The shrink is
 * [Effects.ModifyStats] with negative modifiers rather than a counter effect — the printed line
 * says "until end of turn", which is a floating layer-7c modification and the effect's default
 * `Duration.EndOfTurn`, not a -1/-1 counter.
 */
val BlisterBeetle = card("Blister Beetle") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, target creature gets -1/-1 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-1, -1, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "66"
        artist = "Anthony S. Waters"
        flavorText = "Warriors of the Rip Clan wear their beetle-acid scars proudly, even modifying clothing and armor to better display the trophy."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f42bdeb-5a51-4303-96d8-9722f95d2905.jpg"
    }
}
