package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Bleak Coven Vampires
 * {3}{B}{B}
 * Creature — Vampire Warrior
 * 4/3
 *
 * Metalcraft — When this creature enters, if you control three or more artifacts, target player loses 4 life and you gain 4 life.
 *
 * Metalcraft is an ability word, not a keyword: the artifact count is an ordinary intervening-"if"
 * (CR 603.4), rechecked on resolution, and the word itself survives only in the Oracle text.
 */
val BleakCovenVampires = card("Bleak Coven Vampires") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Warrior"
    power = 4
    toughness = 3
    oracleText = "Metalcraft — When this creature enters, if you control three or more artifacts, target player loses 4 life and you gain 4 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact)
        val victim = target("target player", Targets.Player)
        effect = Effects.Composite(
            Effects.LoseLife(4, victim),
            Effects.GainLife(4),
        )
        description = "Metalcraft — When this creature enters, if you control three or more " +
            "artifacts, target player loses 4 life and you gain 4 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Randis Albion"
        flavorText = "As the shadow of the Mephidross spread over the Tangle, the vampires' territory expanded."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d3386e4-bbd6-4756-b29d-f55619e98d0d.jpg?1783941734"
    }
}
