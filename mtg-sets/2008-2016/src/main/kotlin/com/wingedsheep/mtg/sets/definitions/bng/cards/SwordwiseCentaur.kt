package com.wingedsheep.mtg.sets.definitions.bng.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Swordwise Centaur
 * {G}{G}
 * Creature — Centaur Warrior
 * 3/2
 *
 * Vanilla — no rules text.
 */
val SwordwiseCentaur = card("Swordwise Centaur") {
    manaCost = "{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Centaur Warrior"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "Slawomir Maniak"
        flavorText = "The girl who would become the Champion of the Sun hacked furiously at the practice dummy. At last she stopped, breathing heavily, and looked up at her instructor. \"So much anger,\" said the centaur. \"I will teach you the ways of war, child. But first you must make peace with yourself.\"\n—*The Theriad*"
        imageUri = "https://cards.scryfall.io/normal/front/1/7/1776ebd7-91fc-49e1-a978-f2012162d1cf.jpg?1783939527"
    }
}
