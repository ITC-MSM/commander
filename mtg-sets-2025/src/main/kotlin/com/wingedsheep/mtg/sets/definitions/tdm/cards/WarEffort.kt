package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * War Effort — Tarkir: Dragonstorm #131
 * {3}{R} · Enchantment · Uncommon
 *
 * Creatures you control get +1/+0.
 * Whenever you attack, create a 1/1 red Warrior creature token that's tapped and attacking.
 * Sacrifice it at the beginning of the next end step.
 */
val WarEffort = card("War Effort") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Creatures you control get +1/+0.\n" +
        "Whenever you attack, create a 1/1 red Warrior creature token that's tapped and attacking. " +
        "Sacrifice it at the beginning of the next end step."

    // Creatures you control get +1/+0.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter.AllCreaturesYouControl
        )
    }

    // Whenever you attack, create a 1/1 red Warrior token that's tapped and attacking,
    // sacrificed at the next end step.
    triggeredAbility {
        trigger = Triggers.YouAttack
        effect = CreateTokenEffect(
            count = DynamicAmount.Fixed(1),
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Warrior"),
            tapped = true,
            attacking = true,
            sacrificeAtStep = Step.END,
            imageUri = "https://cards.scryfall.io/normal/front/7/e/7edc0515-a130-45a7-aa09-0e23bba41587.jpg?1742506712"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "131"
        artist = "Ioannis Fiore"
        flavorText = "Community is strength.\n—Tenet of the Decree of Thunder"
        imageUri = "https://cards.scryfall.io/normal/front/d/d/dd7f0413-c009-4c08-b877-9c1b776cbf26.jpg?1743204492"
    }
}
