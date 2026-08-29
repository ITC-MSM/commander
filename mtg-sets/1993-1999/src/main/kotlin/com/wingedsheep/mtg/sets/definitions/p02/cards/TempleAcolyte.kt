package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Temple Acolyte
 * {1}{W}
 * Creature — Human Cleric
 * 1 / 3
 *
 * When this creature enters, you gain 3 life.
 */
val TempleAcolyte = card("Temple Acolyte") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "When this creature enters, you gain 3 life."
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Lubov"
        flavorText = "Young, yes. Inexperienced, yes. Weak? Don't count on it."
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7be7cba2-5673-4bef-aa3d-cbfad8932610.jpg"
    }
}
