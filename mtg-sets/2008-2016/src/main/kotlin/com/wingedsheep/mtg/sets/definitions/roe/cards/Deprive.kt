package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Deprive
 * {U}{U}
 * Instant
 *
 * As an additional cost to cast this spell, return a land you control to its owner's hand.
 * Counter target spell.
 *
 * Modeling notes:
 *  - The bounce is an **additional cost** (CR 601.2f), not an effect: it is paid as the spell is
 *    cast, so a controller with no land on the battlefield cannot cast Deprive at all, and the land
 *    still returns if the counter later fizzles because its target left the stack. That is
 *    `Costs.additional.ReturnToHand`, matching Assay's `AdditionalAtom` → `AtomReturnToHand`.
 *    The Disappearing Act / Familiar's Ruse shape, narrowed from a permanent to a land.
 *  - The filter is the bare [GameObjectFilter.Land]. `CostAtom.ReturnToHand` already scopes its
 *    candidates to permanents *you control*, which is exactly the printed "a land you control" —
 *    so no `.youControl()` is written here, the same as Disappearing Act. Assay's JSON likewise
 *    carries only the `IsLand` predicate.
 *  - "Counter target spell" is the plain [Effects.CounterSpell] over a [Targets.Spell] requirement
 *    — Assay's `TargetObject` with `zone: Stack` and no further predicates. `CounterEffect` reads
 *    the spell's single declared target itself, so the facade takes no handle.
 */
val Deprive = card("Deprive") {
    manaCost = "{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, return a land you control to its owner's hand.\n" +
            "Counter target spell."

    additionalCost(Costs.additional.ReturnToHand(GameObjectFilter.Land))

    spell {
        target("target spell", Targets.Spell)
        effect = Effects.CounterSpell()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Izzy"
        flavorText = "\"That would have brought shame to you as a mage. Tell you what—I'll keep your secret.\"\n—Noyan Dar, Tazeem lullmage"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2efecdd9-bd3a-4b79-92da-6485589d5bde.jpg?1783941998"
    }
}
