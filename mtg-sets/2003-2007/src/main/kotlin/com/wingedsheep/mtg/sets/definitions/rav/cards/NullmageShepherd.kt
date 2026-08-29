package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Nullmage Shepherd
 * {3}{G}
 * Creature — Elf Shaman
 * 2/4
 * Tap four untapped creatures you control: Destroy target artifact or enchantment.
 *
 * [Costs.TapPermanents] already carries "untapped ... you control"; the Shepherd itself is one of
 * the four it may tap, so `excludeSelf` stays at its default.
 */
val NullmageShepherd = card("Nullmage Shepherd") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Shaman"
    oracleText = "Tap four untapped creatures you control: Destroy target artifact or enchantment."
    power = 2
    toughness = 4

    activatedAbility {
        cost = Costs.TapPermanents(count = 4, filter = GameObjectFilter.Creature)
        val t = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "174"
        artist = "Stephen Tappin"
        flavorText = "The shepherds work in secret, seeking out abominations against nature and returning them to earth and dust."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/11f2a6de-1598-4dd9-81a0-9a4f3dd4de0b.jpg"
    }
}
