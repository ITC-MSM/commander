package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * War Elephant
 * {3}{W}
 * Creature — Elephant
 * 2/2
 *
 * Trample; banding (Any creatures with banding, and up to one without, can attack in a band.
 * Bands are blocked as a group. If any creatures with banding you control are blocking or
 * being blocked by a creature, you divide that creature's combat damage, not its controller,
 * among any of the creatures it's being blocked by or is blocking.)
 *
 * Both keywords are fully handled by the combat engine — Banding via CR 702.22
 * (CombatManager / CombatDamageManager / AttackPhaseManager), Trample via CR 702.19 —
 * so the card needs no per-card wiring. Same pattern as Mirage's Noble Elephant.
 */
val WarElephant = card("War Elephant") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant"
    power = 2
    toughness = 2
    oracleText = "Trample; banding (Any creatures with banding, and up to one without, can " +
        "attack in a band. Bands are blocked as a group. If any creatures with banding you " +
        "control are blocking or being blocked by a creature, you divide that creature's " +
        "combat damage, not its controller, among any of the creatures it's being blocked " +
        "by or is blocking.)"

    keywords(Keyword.TRAMPLE, Keyword.BANDING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Kristen Bishop"
        flavorText = "\"When elephants fight it is the grass that suffers.\" —Kikuyu Proverb"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/7416c366-95cc-4799-b6c6-34d8fad8c202.jpg?1562916537"
    }
}
