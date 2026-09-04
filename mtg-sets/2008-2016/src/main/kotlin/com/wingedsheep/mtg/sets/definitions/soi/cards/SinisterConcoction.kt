package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sinister Concoction (Shadows over Innistrad #135)
 * {B}
 * Enchantment
 *
 * {B}, Pay 1 life, Mill a card, Discard a card, Sacrifice this enchantment: Destroy target creature.
 */
val SinisterConcoction = card("Sinister Concoction") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment"
    oracleText = "{B}, Pay 1 life, Mill a card, Discard a card, Sacrifice this enchantment: Destroy target creature."

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{B}"),
            Costs.PayLife(1),
            Costs.MillCard,
            Costs.DiscardCard,
            Costs.SacrificeSelf
        )
        val victim = target("target", Targets.Creature)
        effect = Effects.Destroy(victim)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "135"
        artist = "Zack Stella"
        flavorText = "An old family recipe for an old family grudge."
        imageUri = "https://cards.scryfall.io/normal/front/8/1/815ca911-ccc1-4466-8d12-054b8d241992.jpg?1783937766"
    }
}
