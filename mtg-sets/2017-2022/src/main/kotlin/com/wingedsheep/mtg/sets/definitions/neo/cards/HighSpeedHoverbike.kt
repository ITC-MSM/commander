package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * High-Speed Hoverbike — Kamigawa: Neon Dynasty #247 (canonical printing)
 * {2} · Artifact — Vehicle · 2/2
 *
 * Flash
 * Flying
 * When this Vehicle enters, tap up to one target creature.
 * Crew 1
 *
 * Flash plus the tap trigger is a combat trick on an artifact: flash it in during declare
 * attackers to tap a would-be blocker, then crew it later. "Up to one" makes the target optional,
 * so the trigger still goes on the stack with an empty board.
 */
val HighSpeedHoverbike = card("High-Speed Hoverbike") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Vehicle"
    power = 2
    toughness = 2
    oracleText = "Flash\nFlying\nWhen this Vehicle enters, tap up to one target creature.\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: This " +
        "Vehicle becomes an artifact creature until end of turn.)"

    keywords(Keyword.FLASH, Keyword.FLYING)
    keywordAbility(KeywordAbility.crew(1))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("creature to tap", TargetCreature(optional = true))
        effect = Effects.Tap(t)
        description = "When this Vehicle enters, tap up to one target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "247"
        artist = "Julian Kok Joon Wen"
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c619116-1eae-439d-9b1f-639643458a23.jpg?1783923825"
    }
}
