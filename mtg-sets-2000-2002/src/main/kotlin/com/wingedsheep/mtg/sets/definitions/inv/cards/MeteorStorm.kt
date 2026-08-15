package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Meteor Storm
 * {R}{G}
 * Enchantment
 * {2}{R}{G}, Discard two cards at random: This enchantment deals 4 damage to any target.
 *
 * The "discard two cards at random" portion of the activation cost is paid automatically by the
 * engine (no player selection) via `Costs.DiscardAtRandom(2)`.
 */
val MeteorStorm = card("Meteor Storm") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Enchantment"
    oracleText = "{2}{R}{G}, Discard two cards at random: This enchantment deals 4 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}{G}"), Costs.DiscardAtRandom(2))
        val t = target("any target", Targets.Any)
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "256"
        artist = "John Avon"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/36489b24-f8a8-46b6-b879-0a5ce400a6dc.jpg?1562905963"
    }
}
