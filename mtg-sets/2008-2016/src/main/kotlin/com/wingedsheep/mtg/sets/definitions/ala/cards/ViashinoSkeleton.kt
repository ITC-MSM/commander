package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Viashino Skeleton
 * {3}{R}
 * Creature — Lizard Skeleton
 * 2 / 1
 * {1}{B}, Discard a card: Regenerate this creature.
 *
 * A single activated ability. The printed price is two cost atoms joined by [Costs.Composite] —
 * [Costs.Mana] for the `{1}{B}` and the parameterless [Costs.DiscardCard], whose defaults (one
 * card, any card, chosen by the player) are exactly what "Discard a card" means. The effect is
 * [RegenerateEffect] on [EffectTarget.Self]: regeneration is a shield the engine already knows how
 * to hang on the source, so no new vocabulary is involved.
 */
val ViashinoSkeleton = card("Viashino Skeleton") {
    manaCost = "{3}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Lizard Skeleton"
    power = 2
    toughness = 1
    oracleText = "{1}{B}, Discard a card: Regenerate this creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{B}"), Costs.DiscardCard)
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Cole Eastburn"
        flavorText = "Underneath the Dregscape lay the remains of creatures long extinct from Grixis."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9451a62-31a4-4aaf-beef-2bf149f25ae3.jpg"
    }
}
