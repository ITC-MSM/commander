package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Militia's Pride
 * {1}{W}
 * Kindred Enchantment — Kithkin
 *
 * Whenever a nontoken creature you control attacks, you may pay {W}. If you do, create a 1/1 white
 * Kithkin Soldier creature token that's tapped and attacking.
 *
 * The trigger is per-attacker and `ANY`-bound — one instance for each nontoken attacker, not one
 * for the attack as a whole — so a three-creature alpha strike offers the payment three times.
 * The `nontoken()` half is what stops the tokens it makes from triggering it again.
 *
 * "You may pay {W}. If you do, …" is an optional cost rider on the triggered ability itself
 * ([MayPayManaEffect] → `Gate.MayPay`), resolved as the ability resolves — Customs Depot's shape.
 *
 * The token enters tapped and attacking, so it was never declared as an attacker: it doesn't
 * re-trigger this enchantment, and "whenever a creature attacks" triggers elsewhere don't see it
 * either (CR 508.4).
 *
 * Note: "Tribal" was errata'd to "Kindred" in 2024.
 */
val MilitiasPride = card("Militia's Pride") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Kindred Enchantment — Kithkin"
    oracleText = "Whenever a nontoken creature you control attacks, you may pay {W}. If you do, " +
        "create a 1/1 white Kithkin Soldier creature token that's tapped and attacking."

    triggeredAbility {
        trigger = Triggers.attacks(
            filter = GameObjectFilter.Creature.youControl().nontoken(),
            binding = TriggerBinding.ANY,
        )
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{W}"),
            effect = CreateTokenEffect(
                power = 1,
                toughness = 1,
                colors = setOf(Color.WHITE),
                creatureTypes = setOf("Kithkin", "Soldier"),
                tapped = true,
                attacking = true,
                imageUri = "https://cards.scryfall.io/normal/front/a/d/ad29eb21-7ee3-4a67-9601-a62ea0cbe4c0.jpg?1783942839",
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "30"
        artist = "Larry MacDougall"
        flavorText = "If you pick a fight with one kithkin, be ready to fight them all."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53c8cde1-e447-422e-aed0-2571a77d3d29.jpg?1783942912"
    }
}
