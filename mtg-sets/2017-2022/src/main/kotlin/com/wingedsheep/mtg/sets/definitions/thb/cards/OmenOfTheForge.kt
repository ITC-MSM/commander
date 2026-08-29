package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Omen of the Forge
 * {1}{R}
 * Enchantment
 *
 * Flash
 * When this enchantment enters, it deals 2 damage to any target.
 * {2}{R}, Sacrifice this enchantment: Scry 2.
 *
 * The red member of the Omen cycle — a flash Shock that later sacrifices itself to smooth the next
 * draw. "Any target" is [Targets.Any]; the damage is sourced from the enchantment itself, which is
 * the facade's default, so no explicit `damageSource` is written.
 */
val OmenOfTheForge = card("Omen of the Forge") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Flash\n" +
        "When this enchantment enters, it deals 2 damage to any target.\n" +
        "{2}{R}, Sacrifice this enchantment: Scry 2."

    keywords(Keyword.FLASH)

    // When this enchantment enters, it deals 2 damage to any target.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target", Targets.Any)
        effect = Effects.DealDamage(2, victim)
    }

    // {2}{R}, Sacrifice this enchantment: Scry 2.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}{R}"),
            Costs.SacrificeSelf
        )
        effect = Effects.Scry(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "145"
        artist = "Piotr Dura"
        flavorText = "\"My time will come, when all the world will be reforged in the fires of my invention.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/0/70388773-709b-4cd8-a8b7-56093fd77a1d.jpg"
    }
}
