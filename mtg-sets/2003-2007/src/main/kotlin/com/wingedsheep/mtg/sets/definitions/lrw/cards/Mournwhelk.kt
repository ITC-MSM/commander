package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Mournwhelk
 * {6}{B}
 * Creature — Elemental
 * 3/3
 * When this creature enters, target player discards two cards.
 * Evoke {3}{B}
 */
val Mournwhelk = card("Mournwhelk") {
    manaCost = "{6}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Elemental"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, target player discards two cards.\n" +
        "Evoke {3}{B} (You may cast this spell for its evoke cost. If you do, it's sacrificed " +
        "when it enters.)"

    evoke = "{3}{B}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val player = target("target player", Targets.Player)
        effect = Effects.Discard(2, player)
        description = "target player discards two cards."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Jeremy Jarvis"
        flavorText = "It hoards Lorwyn's rare sorrows."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3a62a282-5f94-444b-9095-4c36fabdad57.jpg?1783942886"
    }
}
