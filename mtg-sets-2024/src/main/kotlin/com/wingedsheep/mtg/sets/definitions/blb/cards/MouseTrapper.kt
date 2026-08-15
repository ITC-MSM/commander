package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mouse Trapper
 * {2}{W}
 * Creature — Mouse Soldier
 * 3/2
 *
 * Flash
 * Valiant — Whenever this creature becomes the target of a spell or ability
 * you control for the first time each turn, tap target creature an opponent controls.
 */
val MouseTrapper = card("Mouse Trapper") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Mouse Soldier"
    power = 3
    toughness = 2
    oracleText = "Flash\nValiant — Whenever this creature becomes the target of a spell or ability you control for the first time each turn, tap target creature an opponent controls."

    keywords(Keyword.FLASH)

    // Valiant: first time targeted by your spell/ability each turn → tap opponent's creature
    triggeredAbility {
        trigger = Triggers.Valiant
        val t = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "22"
        artist = "Jakub Kasper"
        flavorText = "\"I'm not here to hurt you, but I will stop you from hurting others.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8ba1bc5a-03e7-44ec-893e-44042cbc02ef.jpg?1721425888"

        ruling("2024-07-26", "Valiant abilities will resolve before the spell or ability that caused them to trigger.")
    }
}
