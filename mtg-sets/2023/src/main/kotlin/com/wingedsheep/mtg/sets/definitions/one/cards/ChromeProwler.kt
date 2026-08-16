package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Chrome Prowler
 * {2}{U}
 * Artifact Creature — Phyrexian Cat
 * 3/2
 *
 * Flash
 * When this creature enters, tap target creature an opponent controls.
 */
val ChromeProwler = card("Chrome Prowler") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Phyrexian Cat"
    power = 3
    toughness = 2
    oracleText = "Flash\n" +
        "When this creature enters, tap target creature an opponent controls."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.Tap(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a94378a2-cbde-4adf-b889-43e90fc6ba28.jpg?1783918069"
    }
}
