package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Big Game Hunter
 * {1}{B}{B}
 * Creature — Human Rebel Assassin
 * 1/1
 * When this creature enters, destroy target creature with power 4 or greater. It can't be regenerated.
 * Madness {B}
 *
 * "It can't be regenerated" is `noRegenerate` on the destroy, which composes a
 * `CantBeRegeneratedEffect` in front of the move rather than being a separate printed sentence.
 */
val BigGameHunter = card("Big Game Hunter") {
    manaCost = "{1}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Rebel Assassin"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, destroy target creature with power 4 or greater. It can't be regenerated.\n" +
        "Madness {B} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    madness("{B}")

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.powerAtLeast(4)))
        )
        effect = Effects.Destroy(t, noRegenerate = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "63"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a61f38a9-6f15-4186-a602-78cdb00f2d75.jpg"
    }
}
