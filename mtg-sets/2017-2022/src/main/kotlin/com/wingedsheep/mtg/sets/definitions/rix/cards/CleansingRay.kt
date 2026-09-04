package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Cleansing Ray
 * {1}{W}
 * Sorcery
 *
 * Choose one —
 * • Destroy target Vampire.
 * • Destroy target enchantment.
 *
 * Each mode names its own target, so the requirement is declared inside the mode. "Target
 * Vampire" is the bare tribal noun — a noncreature Vampire permanent is legal too, hence
 * [GameObjectFilter.Permanent] rather than `.Creature`.
 */
val CleansingRay = card("Cleansing Ray") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• Destroy target Vampire.\n" +
        "• Destroy target enchantment."

    spell {
        modal {
            mode("Destroy target Vampire") {
                val vampire = target(
                    "target Vampire",
                    TargetPermanent(
                        filter = TargetFilter(
                            GameObjectFilter.Permanent.withSubtype(Subtype.VAMPIRE)
                        )
                    )
                )
                effect = Effects.Destroy(vampire)
            }
            mode("Destroy target enchantment") {
                val enchantment = target("target enchantment", Targets.Enchantment)
                effect = Effects.Destroy(enchantment)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "4"
        artist = "Anthony Palumbo"
        flavorText = "\"The sun creates, sustains, and consumes so it can create again.\"\n" +
            "—Caparocti Sunborn"
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7bf94184-e745-469a-9d55-af0ddacbb9cf.jpg?1783935341"
    }
}
