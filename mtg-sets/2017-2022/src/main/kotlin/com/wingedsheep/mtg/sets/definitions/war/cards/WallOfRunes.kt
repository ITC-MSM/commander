package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Runes
 * {U}
 * Creature — Wall
 * 0/4
 * Defender (This creature can't attack.)
 * When this creature enters, scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 *
 * A one-mana wall that replaces a little of its own card selection: the scry is an untargeted
 * enters trigger, so it always resolves.
 */
val WallOfRunes = card("Wall of Runes") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Wall"
    oracleText = "Defender (This creature can't attack.)\n" +
        "When this creature enters, scry 1. (Look at the top card of your library. You may put that card on the bottom.)"
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Zezhou Chen"
        flavorText = "\"It's strangely satisfying seeing an undead killing machine from another world standing befuddled in front of a wall of text.\"\n—Lavinia"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96613089-3508-429a-9f90-23168d56bbe7.jpg"
    }
}
