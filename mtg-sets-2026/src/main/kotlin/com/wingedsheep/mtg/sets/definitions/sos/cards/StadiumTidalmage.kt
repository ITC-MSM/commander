package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Stadium Tidalmage
 * {2}{U}{R}
 * Creature — Djinn Sorcerer
 * 4/4
 * Whenever this creature enters or attacks, you may draw a card. If you do, discard a card.
 *
 * "Enters or attacks" is two separate triggered abilities sharing one effect (see
 * Dragonhawk, Fate's Tempest). The optional loot is `MayEffect(Patterns.Hand.loot())` —
 * the "if you do, discard" semantics live in `loot()` (draw, then discard), and the
 * outer `MayEffect` is the "you may" choice (see Jeskai Elder).
 */
val StadiumTidalmage = card("Stadium Tidalmage") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Creature — Djinn Sorcerer"
    power = 4
    toughness = 4
    oracleText = "Whenever this creature enters or attacks, you may draw a card. If you do, discard a card."

    val lootEffect: Effect = MayEffect(Patterns.Hand.loot())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = lootEffect
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = lootEffect
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "232"
        artist = "Ioannis Fiore"
        flavorText = "The Prismari team takes the direction to \"flood the zone\" literally."
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a689289e-7141-4950-8a87-82e9bd6846fe.jpg?1775938619"
    }
}
