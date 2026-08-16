package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Urn of Godfire
 * {1}
 * Artifact
 * {2}: Add one mana of any color.
 * {6}, {T}, Sacrifice this artifact: Destroy target creature or enchantment.
 */
val UrnOfGodfire = card("Urn of Godfire") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}: Add one mana of any color.\n" +
        "{6}, {T}, Sacrifice this artifact: Destroy target creature or enchantment."

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.Tap, Costs.SacrificeSelf)
        val victim = target("target creature or enchantment", Targets.CreatureOrEnchantment)
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "266"
        artist = "Ovidio Cartagena"
        flavorText = "Ephara blessed the defenders of Theros with godfire, a Nyx-infused " +
            "incendiary substance that burned without fuel."
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff5f302e-d884-4995-ba3c-8c22f9045318.jpg?1783916934"
    }
}
