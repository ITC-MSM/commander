package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AttackTax
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Propaganda
 * {2}{U}
 * Enchantment
 * Creatures can't attack you unless their controller pays {2} for each creature they control that's attacking you.
 *
 * The whole card is one [AttackTax] static — the engine's combat-tax rules already charge the
 * per-attacker amount to each attacking creature's controller, so a flat [DynamicAmount.Fixed] of 2
 * is the entire card (the same shape Windborn Muse uses for the identical line).
 */
val Propaganda = card("Propaganda") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Creatures can't attack you unless their controller pays {2} for each creature they control that's attacking you."

    staticAbility {
        ability = AttackTax(amountPerAttacker = DynamicAmount.Fixed(2))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "80"
        artist = "Jeff Miracola"
        flavorText = "\"You've failed Gerrard. You've failed the Legacy. You've failed yourself. I can do no more.\"\n—Volrath, to Karn"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f67dde4d-3df1-480d-a8b8-ab22c768bb12.jpg?1783946652"
    }
}
