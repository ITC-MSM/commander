package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dawnfluke
 * {3}{W}
 * Creature — Elemental
 * 0/3
 * Flash
 * When this creature enters, prevent the next 3 damage that would be dealt to any target this turn.
 * Evoke {W}
 */
val Dawnfluke = card("Dawnfluke") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elemental"
    power = 0
    toughness = 3
    oracleText = "Flash\nWhen this creature enters, prevent the next 3 damage that would be dealt " +
        "to any target this turn.\nEvoke {W} (You may cast this spell for its evoke cost. If you " +
        "do, it's sacrificed when it enters.)"

    keywords(Keyword.FLASH)

    evoke = "{W}"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val recipient = target("any target", Targets.Any)
        effect = Effects.PreventNextDamage(3, recipient)
        description = "prevent the next 3 damage that would be dealt to any target this turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8968bc0-518f-4c41-bf00-2bf295065e33.jpg?1783942916"
    }
}
