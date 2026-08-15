package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.impending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Overlord of the Boilerbilges
 * {4}{R}{R}
 * Enchantment Creature — Avatar Horror
 * 5/5
 *
 * Impending 4—{2}{R}{R} (If you cast this spell for its impending cost, it enters with four
 * time counters and isn't a creature until the last is removed. At the beginning of your
 * end step, remove a time counter from it.)
 *
 * Whenever this permanent enters or attacks, it deals 4 damage to any target.
 *
 * Impending is wired by the `impending(n, cost)` DSL helper (CR 702.176): the alternative
 * cost, the "isn't a creature while it has a time counter" type-removing static ability, and
 * the "remove a time counter at the beginning of your end step" trigger. The engine places
 * the four time counters when the spell is cast for its impending cost.
 *
 * The "enters or attacks" ability is one effect referenced by two triggered abilities (an
 * enters-the-battlefield trigger and an attacks trigger), each declaring its own "any target"
 * so the controller picks the target as each trigger resolves.
 */
val OverlordOfTheBoilerbilges = card("Overlord of the Boilerbilges") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Avatar Horror"
    oracleText = "Impending 4—{2}{R}{R} (If you cast this spell for its impending cost, it enters with four time counters and isn't a creature until the last is removed. At the beginning of your end step, remove a time counter from it.)\n" +
        "Whenever this permanent enters or attacks, it deals 4 damage to any target."
    power = 5
    toughness = 5

    impending(4, "{2}{R}{R}")

    // "It deals 4 damage to any target." Shared by the enters and attacks triggers.
    val dealFour: Effect = Effects.DealDamage(
        4,
        EffectTarget.ContextTarget(0),
        damageSource = EffectTarget.Self
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = Targets.Any
        effect = dealFour
        description = "Whenever this permanent enters, it deals 4 damage to any target."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        target = Targets.Any
        effect = dealFour
        description = "Whenever this permanent attacks, it deals 4 damage to any target."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "146"
        artist = "Helge C. Balzer"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d58d3545-043a-457a-8324-facc3c2363ec.jpg?1726286391"
        ruling("2024-09-20", "If you choose to pay the impending cost rather than the mana cost, you're still casting the spell. It goes on the stack and can be responded to, countered, and so on.")
        ruling("2024-09-20", "If an object enters as a copy of a permanent that was cast with its impending cost, it won't enter with time counters, and it will be a creature.")
    }
}
