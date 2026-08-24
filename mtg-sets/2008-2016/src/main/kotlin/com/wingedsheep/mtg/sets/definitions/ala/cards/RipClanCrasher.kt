package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rip-Clan Crasher
 * {R}{G}
 * Creature — Human Warrior
 * 2 / 2
 * Haste
 *
 * A single printed keyword and no script: `keywords(Keyword.HASTE)` stamps both the projected
 * keyword set the summoning-sickness check reads and the simple keyword ability the rules text
 * renders from.
 */
val RipClanCrasher = card("Rip-Clan Crasher") {
    manaCost = "{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Haste"

    keywords(Keyword.HASTE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Justin Sweet"
        flavorText = "If you breathe, she will fight you. If you breathe fire, she must fight you."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d61c4a0-054b-479e-82d9-dc60c5c708e2.jpg"
    }
}
