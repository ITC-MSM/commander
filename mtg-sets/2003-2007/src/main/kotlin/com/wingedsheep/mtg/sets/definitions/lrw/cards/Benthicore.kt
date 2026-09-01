package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Benthicore
 * {6}{U}
 * Creature — Elemental
 * 5/5
 * When this creature enters, create two 1/1 blue Merfolk Wizard creature tokens.
 * Tap two untapped Merfolk you control: Untap this creature. It gains shroud until end of turn.
 *
 * The two tokens it brings along are exactly the two Merfolk its own ability wants, so it arrives
 * able to pay for itself once.
 *
 * The cost is the Summon the School shape — `Costs.TapPermanents(count = 2, …)`. Both restrictions
 * the oracle text spells out are the engine's, not the filter's: `CostPaymentService` builds the
 * candidate pool with `controlledUntapped`, so "untapped" and "you control" are enforced for
 * affordability, for the prompt, and for validation from one place. Summoning sickness never
 * applies to a permanent tapped to pay a cost (CR 302.6 is about the `{T}` symbol), so a
 * just-arrived Merfolk token can pay.
 *
 * Note the ability has **no `{T}` of its own** — Benthicore itself is never tapped to activate it,
 * which is what lets an already-tapped Benthicore untap by paying, and lets a player untap it
 * repeatedly with enough Merfolk. Benthicore is an Elemental, not a Merfolk, so it can never be
 * part of its own cost regardless.
 */
val Benthicore = card("Benthicore") {
    manaCost = "{6}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 5
    toughness = 5
    oracleText = "When this creature enters, create two 1/1 blue Merfolk Wizard creature tokens.\n" +
        "Tap two untapped Merfolk you control: Untap this creature. It gains shroud until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Merfolk", "Wizard"),
            imageUri = "https://cards.scryfall.io/normal/front/5/2/526da544-23dd-42b8-8c00-c3609eea4489.jpg?1783942838",
        )
        description = "create two 1/1 blue Merfolk Wizard creature tokens."
    }

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 2,
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.MERFOLK),
        )
        effect = Effects.Composite(
            Effects.Untap(EffectTarget.Self),
            Effects.GrantKeyword(Keyword.SHROUD, EffectTarget.Self, Duration.EndOfTurn),
        )
        description = "Untap this creature. It gains shroud until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "53"
        artist = "Jim Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f240d225-9fbf-438a-93c3-5fef325035e8.jpg?1783942905"
    }
}
