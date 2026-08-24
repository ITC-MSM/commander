package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Mox Opal — Scars of Mirrodin #179
 * {0} · Legendary Artifact
 *
 * Metalcraft — {T}: Add one mana of any color. Activate only if you control three or more artifacts.
 *
 * A mana ability (CR 605.1a) — it adds mana, doesn't target and never touches a library — so it
 * uses no stack and may be activated while paying a cost. The metalcraft clause is an ordinary
 * [ActivationRestriction.OnlyIfCondition]: it gates *activation*, so it is checked when the ability
 * is activated, not on resolution. Colour identity stays colourless: "any color" adds nothing to it
 * (CR 903.4).
 */
val MoxOpal = card("Mox Opal") {
    manaCost = "{0}"
    colorIdentity = ""
    typeLine = "Legendary Artifact"
    oracleText = "Metalcraft — {T}: Add one mana of any color. Activate only if you control three or more artifacts."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact)
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "179"
        artist = "Volkan Baǵa"
        flavorText = "The suns of Mirrodin have shone upon perfection only once."
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6be9b1d5-9ab8-4adb-ba54-2c0117e842fa.jpg?1783941702"
    }
}
