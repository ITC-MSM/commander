package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Capashen Unicorn
 * {1}{W}
 * Creature — Unicorn (1/2)
 *
 * {1}{W}, {T}, Sacrifice this creature: Destroy target artifact or enchantment.
 */
val CapashenUnicorn = card("Capashen Unicorn") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Unicorn"
    power = 1
    toughness = 2
    oracleText = "{1}{W}, {T}, Sacrifice this creature: Destroy target artifact or enchantment."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Jerry Tiritilli"
        flavorText = "Capashen riders were stern and humorless even before their ancestral home " +
            "was reduced to rubble."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec3e5741-88d7-4837-9b43-ba8304d9ee74.jpg?1562942469"
    }
}
