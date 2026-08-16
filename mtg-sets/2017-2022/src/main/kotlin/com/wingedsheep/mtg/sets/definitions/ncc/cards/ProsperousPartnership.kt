package com.wingedsheep.mtg.sets.definitions.ncc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Prosperous Partnership
 * {1}{R}{W}
 * Enchantment
 *
 * When this enchantment enters, create two 1/1 green and white Citizen creature tokens.
 * Tap three untapped creatures you control: Create a Treasure token.
 *
 * The activation cost is [Costs.TapPermanents] — tapping the creatures is the cost, so it is paid
 * on activation and the creatures need not be untapped when the ability resolves. It is not a
 * mana ability (it makes a Treasure, not mana), so it uses the stack.
 */
val ProsperousPartnership = card("Prosperous Partnership") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "WR"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create two 1/1 green and white Citizen creature " +
        "tokens.\n" +
        "Tap three untapped creatures you control: Create a Treasure token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN, Color.WHITE),
            creatureTypes = setOf("Citizen"),
            count = 2
        )
    }

    activatedAbility {
        cost = Costs.TapPermanents(3, GameObjectFilter.Creature)
        effect = Effects.CreateTreasure()
        description = "Create a Treasure token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "78"
        artist = "Evyn Fong"
        flavorText = "Partners in love, partners in life, and partners in crime."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a50267d1-f4b7-494f-b50b-cfb08b760b47.jpg"
    }
}
