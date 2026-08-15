package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource

/**
 * Light of Judgment
 * {4}{R}
 * Instant
 *
 * Light of Judgment deals 6 damage to target creature. Destroy up to one Equipment attached
 * to that creature.
 *
 * One target creature, two sequential clauses:
 *  1. [Effects.DealDamage]`(6)` to the target.
 *  2. A non-targeted "up to one" destroy, modeled as a Gather → choose-up-to-1 → destroy
 *     pipeline. [CardSource.AttachedTo] gathers the Equipment attached to the *same* target
 *     (state-based actions that would move the lethally-damaged creature only run after the
 *     spell finishes resolving, so its Equipment is still attached here), then the controller
 *     may pick at most one to destroy via on-battlefield selection (`useTargetingUI`). The
 *     destroy is intentionally *not* a target — it ignores targeting restrictions and is chosen
 *     at resolution, per the oracle wording.
 */
val LightOfJudgment = card("Light of Judgment") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Light of Judgment deals 6 damage to target creature. Destroy up to one " +
        "Equipment attached to that creature."

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.DealDamage(6, creature),
            Effects.Pipeline {
                val onCreature = gather(
                    CardSource.AttachedTo(
                        host = creature,
                        filter = GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT),
                    )
                )
                val chosen = chooseUpTo(
                    count = 1,
                    from = onCreature,
                    useTargetingUI = true,
                    prompt = "Destroy up to one Equipment attached to that creature",
                )
                destroy(chosen)
            },
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Daniel Landerman"
        flavorText = "\"I was there... I saw when Kefka used his Light of Judgment to torch the " +
            "village of Mobliz, far to the east.\"\n—Tzen resident"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98bb716d-ca66-445f-9cb3-0fc656c8ebff.jpg?1748706299"
    }
}
