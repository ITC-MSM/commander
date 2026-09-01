package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AttackTax
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Ghostly Prison
 * {2}{W}
 * Enchantment
 *
 * Creatures can't attack you unless their controller pays {2} for each creature they control that's
 * attacking you.
 *
 * The whole card is one [AttackTax] static — the engine's combat-tax rules already charge the
 * per-attacker amount to each attacking creature's controller, so a flat [DynamicAmount.Fixed] of
 * 2 is the entire card. Same shape as Propaganda, which prints the identical line in blue.
 */
val GhostlyPrison = card("Ghostly Prison") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Creatures can't attack you unless their controller pays {2} for each creature they control that's attacking you."

    staticAbility {
        ability = AttackTax(amountPerAttacker = DynamicAmount.Fixed(2))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "10"
        artist = "Lars Grant-West"
        flavorText = "Destroyed in one of the first battles of the Kami War, the town of Reito still grieved."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/82d7de2b-c909-48dc-9ab7-c4a8328e37bb.jpg?1783944341"
    }
}
