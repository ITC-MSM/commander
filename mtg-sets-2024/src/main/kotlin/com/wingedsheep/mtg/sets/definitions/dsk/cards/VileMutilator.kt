package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vile Mutilator
 * {5}{B}{B}
 * Creature — Demon
 * 6/5
 * As an additional cost to cast this spell, sacrifice a creature or enchantment.
 * Flying, trample
 * When this creature enters, each opponent sacrifices a nontoken enchantment of their choice, then
 * sacrifices a nontoken creature of their choice.
 *
 * Modeled entirely from existing primitives:
 *
 * - The additional cost is the standard [Costs.additional.SacrificePermanent] over
 *   [GameObjectFilter.CreatureOrEnchantment] (same shape as Final Vengeance / Embrace Oblivion).
 * - Flying and trample are plain keywords.
 * - The ETB is a [Triggers.EntersBattlefield] whose effect is a two-step [Effects.Composite] in the
 *   printed order: first each opponent sacrifices a nontoken enchantment, *then* each opponent
 *   sacrifices a nontoken creature. Each step is an [Effects.Sacrifice] over the matching
 *   `.nontoken()` filter targeting [Player.EachOpponent]; because the sacrifice is forced onto a
 *   player (not a chosen target), that player picks which permanent to sacrifice — exactly the
 *   "of their choice" semantics. Token permanents are excluded by the `.nontoken()` filter, and the
 *   ordering of the two `Sacrifice` effects within the composite preserves "enchantment, then
 *   creature".
 */
val VileMutilator = card("Vile Mutilator") {
    manaCost = "{5}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 6
    toughness = 5
    oracleText = "Flying, trample\n" +
        "When this creature enters, each opponent sacrifices a nontoken enchantment of their " +
        "choice, then sacrifices a nontoken creature of their choice."

    // As an additional cost to cast this spell, sacrifice a creature or enchantment.
    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.CreatureOrEnchantment))

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            // First: each opponent sacrifices a nontoken enchantment of their choice.
            Effects.Sacrifice(
                filter = GameObjectFilter.Enchantment.nontoken(),
                target = EffectTarget.PlayerRef(Player.EachOpponent),
            ),
            // Then: each opponent sacrifices a nontoken creature of their choice.
            Effects.Sacrifice(
                filter = GameObjectFilter.Creature.nontoken(),
                target = EffectTarget.PlayerRef(Player.EachOpponent),
            ),
        )
        description = "When this creature enters, each opponent sacrifices a nontoken enchantment of " +
            "their choice, then sacrifices a nontoken creature of their choice."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "122"
        artist = "Néstor Ossandón Leal"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/383fe040-98bb-40f3-b809-48c429b83f47.jpg?1726286303"
        ruling(
            "2024-09-20",
            "While resolving Vile Mutilator's last ability, the next opponent in turn order chooses a " +
                "nontoken enchantment they control, then each other opponent in turn order does the same. " +
                "Then each of the chosen nontoken enchantments are sacrificed simultaneously. Finally, " +
                "repeat this process for nontoken creatures."
        )
    }
}
