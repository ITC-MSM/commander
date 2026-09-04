package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Shared Discovery
 * {U}
 * Sorcery
 *
 * As an additional cost to cast this spell, tap four untapped creatures you control.
 * Draw three cards.
 *
 * Modeling notes:
 *  - The tap is an **additional cost** (CR 601.2f), not an effect — a controller without four
 *    untapped creatures simply cannot cast the spell, and the creatures stay tapped even if the
 *    draw is somehow replaced. `Costs.additional.TapPermanents(count = 4, ...)` is Assay's
 *    `AdditionalAtom` → `AtomTapPermanents` with `count: 4`, the Fear of Exposure / Gaze of Justice
 *    shape.
 *  - Tapping this way is a cost, not the `{T}` symbol, so summoning sickness (CR 302.6) does not
 *    apply to the creatures chosen; only untapped ones are legal choices (CR 701.26a), which is
 *    what `.untapped()` states explicitly on the filter.
 *  - **Divergence from Assay's JSON, deliberate.** Assay renders the cost filter as bare
 *    `IsCreature`, dropping the printed "you control". A cost can only ever tap permanents its
 *    payer controls, so that is Assay under-specifying rather than the card meaning something
 *    wider; the printed restriction is written here as `.youControl()`.
 *  - "Draw three cards" is a flat [Effects.DrawCards] with no target — the caster draws.
 */
val SharedDiscovery = card("Shared Discovery") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, tap four untapped creatures you control.\n" +
            "Draw three cards."

    additionalCost(
        Costs.additional.TapPermanents(
            count = 4,
            filter = GameObjectFilter.Creature.untapped().youControl()
        )
    )

    spell {
        effect = Effects.DrawCards(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Ryan Pancoast"
        flavorText = "Riches must be divided, but real wealth can be shared."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec0b99f1-d706-4332-a1c2-86d789919069.jpg?1783941991"
    }
}
