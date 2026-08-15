package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.MarkExileOnDeathEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Betrayer's Bargain
 * {1}{R}
 * Instant
 * As an additional cost to cast this spell, sacrifice a creature or enchantment or pay {2}.
 * Betrayer's Bargain deals 5 damage to target creature. If that creature would die this turn,
 * exile it instead.
 *
 * Two pieces, both modeled with existing primitives:
 *
 * - The binary additional cost (sacrifice a creature or enchantment, OR pay {2}) is the
 *   [LashOfTheBalrog] shape: a non-modal [ModalEffect.chooseOne] whose two modes carry the same
 *   target requirement and effect but different costs — one sacrifices a creature/enchantment, the
 *   other pays {2}. `countsAsModalSpell = false` because there is no "Choose one —" on the card; the
 *   fork is purely a cost choice the caster makes at cast time.
 * - "Deals 5 damage … if that creature would die this turn, exile it instead" is the [AgateAssault]
 *   shape: [MarkExileOnDeathEffect] sets up the die→exile replacement on the target first, then
 *   [Effects.DealDamage] deals the 5. Per the Scryfall ruling the replacement applies to *any* death
 *   this turn (not just death from this damage), which is exactly what `MarkExileOnDeathEffect` does.
 */
val BetrayersBargain = card("Betrayer's Bargain") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature or enchantment or " +
        "pay {2}.\nBetrayer's Bargain deals 5 damage to target creature. If that creature would die " +
        "this turn, exile it instead."

    val creature = EffectTarget.ContextTarget(0)
    val body = MarkExileOnDeathEffect(creature).then(Effects.DealDamage(5, creature))

    spell {
        effect = ModalEffect.chooseOne(
            // Sacrifice a creature or enchantment
            Mode(
                effect = body,
                targetRequirements = listOf(Targets.Creature),
                description = "Sacrifice a creature or enchantment — deal 5 damage to target creature",
                additionalCosts = listOf(
                    Costs.additional.SacrificePermanent(filter = GameObjectFilter.CreatureOrEnchantment)
                )
            ),
            // Pay {2}
            Mode(
                effect = body,
                targetRequirements = listOf(Targets.Creature),
                description = "Pay {2} — deal 5 damage to target creature",
                additionalManaCost = "{2}"
            ),
            countsAsModalSpell = false
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "126"
        artist = "Billy Christian"
        flavorText = "\"This is my way out. You'll have to find your own.\"\n—Winter, to Niko"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/7956ae00-8f0c-48f0-8110-19ff53863876.jpg?1726286318"
        ruling(
            "2024-09-20",
            "If Betrayer's Bargain resolves, its effect causes the creature to be exiled any time it " +
                "would die that turn, not just if it would die as a result of damage dealt by Betrayer's Bargain."
        )
    }
}
