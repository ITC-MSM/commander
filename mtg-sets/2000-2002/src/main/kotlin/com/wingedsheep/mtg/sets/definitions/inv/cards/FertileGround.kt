package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalManaOnTap
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Fertile Ground
 * {1}{G}
 * Enchantment — Aura
 * Enchant land
 * Whenever enchanted land is tapped for mana, its controller adds an additional one mana of any color.
 *
 * Invasion engine gap #3. Reuses the existing [AdditionalManaOnTap] tap-bonus static, extended with
 * `anyColor = true` so the bonus is one mana of any color the controller chooses each time the land
 * is tapped (rather than Elvish Guidance's fixed {G}). On a manual tap the controller is prompted
 * for the color; when auto-tapping for a cost the solver treats the bonus as flexible.
 */
val FertileGround = card("Fertile Ground") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant land\nWhenever enchanted land is tapped for mana, its controller adds an additional one mana of any color."

    auraTarget = Targets.Land

    staticAbility {
        ability = AdditionalManaOnTap(amount = DynamicAmount.Fixed(1), anyColor = true)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "188"
        artist = "Carl Critchlow"
        flavorText = "As Phyrexians descended, Multani paused to reflect on the beauty that might never be seen again."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/789e3582-b541-4916-ac7e-015214d7a27a.jpg?1562919190"
    }
}
