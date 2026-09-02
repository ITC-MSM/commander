package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ProtectionScope

/**
 * Burrenton Forge-Tender
 * {W}
 * Creature — Kithkin Wizard
 * 1/1
 * Protection from red
 * Sacrifice this creature: Prevent all damage a red source of your choice would deal this turn.
 *
 * Modelling notes:
 * - The prevention has **no recipient clause** — it stops the chosen source's damage to anything,
 *   not just to its controller. That is what `PreventAllDamageFromChosenSourceMatching` expresses
 *   (`direction = FromTarget`), the same shield Mourner's Shield installs.
 * - **The ability doesn't target** (2017-11-17 ruling): the source is chosen as the ability
 *   resolves, and the ability can be activated with no red source on the board at all. A
 *   `ChosenSourceMatching` eligibility filter is a choice restriction, not a target requirement, so
 *   that falls out for free.
 * - `nextInstanceOnly` stays false — this is an all-damage-for-the-turn shield, not a Circle of
 *   Protection's single instance.
 */
val BurrentonForgeTender = card("Burrenton Forge-Tender") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Wizard"
    power = 1
    toughness = 1
    oracleText = "Protection from red\n" +
        "Sacrifice this creature: Prevent all damage a red source of your choice would deal this turn."

    keywordAbility(KeywordAbility.Protection(ProtectionScope.Color(Color.RED)))

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.PreventAllDamageFromChosenSourceMatching(
            GameObjectFilter.Any.withColor(Color.RED)
        )
        description = "Sacrifice this creature: Prevent all damage a red source of your choice " +
            "would deal this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "7"
        artist = "Chuck Lukacs"
        flavorText = "\"We are a clachan of smiths. The forge is as comfortable to us as a small " +
            "fire during a cool winter's evening.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c000c3e4-d71a-43c8-8ded-f3da54bc088d.jpg?1783942917"
        ruling(
            "2017-11-17",
            "If the red source you chose changes colors before it deals damage, the " +
                "damage-prevention effect doesn't apply."
        )
        ruling(
            "2017-11-17",
            "The last ability of Burrenton Forge-Tender doesn't target anything. You choose a " +
                "source of damage as the ability resolves, if able. You can activate it even if " +
                "there is no source to choose."
        )
        ruling(
            "2017-11-17",
            "If a permanent spell is chosen as the source of damage, Burrenton Forge-Tender's " +
                "prevention effect continues to apply for the rest of the turn to the permanent " +
                "that spell becomes once it has entered the battlefield."
        )
    }
}
