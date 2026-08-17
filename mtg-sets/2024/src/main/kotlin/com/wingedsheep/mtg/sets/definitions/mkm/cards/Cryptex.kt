package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cryptex — Murders at Karlov Manor #251
 * {2} · Artifact · Rare
 *
 * {T}, Collect evidence 3: Add one mana of any color. Put an unlock counter on this artifact.
 * Sacrifice this artifact: Surveil 3, then draw three cards. Activate only if this artifact has
 * five or more unlock counters on it.
 *
 * A five-turn clock that pays rent the whole way: every tap is a Cabal Coffers-grade colour fixer
 * that also advances the combination, and the fifth one opens it for three cards.
 *
 * **The first ability really is a mana ability**, and the printed ruling says so: it doesn't use the
 * stack and can't be responded to. That is `manaAbility = true`, which also fixes the timing rule —
 * the rider (a counter) doesn't change the classification, because CR 605.1a asks only whether the
 * ability could add mana and whether it targets, not whether it does anything *else*. The counter
 * therefore lands at a moment nobody can interact with, exactly like the ICE painlands' damage
 * rider. Collect evidence sits in the cost alongside `{T}` as an ordinary cost atom, so CR 701.59b
 * applies: a graveyard that can't reach total mana value 3 makes the whole ability unactivatable
 * rather than offering a collection the player couldn't complete.
 *
 * **The unlock counter** is a new passive named counter ([Counters.UNLOCK]) with no inherent rule of
 * its own — the same accumulate-then-threshold shape as `Counters.POINT` and `Counters.PLAN`, and
 * like them the payoff sacrifices its own source, so the "five or more" gate can never fire twice.
 * The gate is [ActivationRestriction.OnlyIfCondition] over
 * [Conditions.SourceCounterCountAtLeast], which reads the source's counters live rather than at
 * enters-time, so counters added by anything else (proliferate, a counter-doubler) count too.
 *
 * Note the second ability's cost is only the sacrifice — no mana, no tap. A Cryptex that is already
 * tapped, or summoning-sick, can still be cracked; the five taps it took to get there are the cost.
 */
val Cryptex = card("Cryptex") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}, Collect evidence 3: Add one mana of any color. Put an unlock counter on " +
        "this artifact. (To collect evidence 3, exile cards with total mana value 3 or greater " +
        "from your graveyard.)\n" +
        "Sacrifice this artifact: Surveil 3, then draw three cards. Activate only if this " +
        "artifact has five or more unlock counters on it."

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.CollectEvidence(3))
        manaAbility = true
        effect = Effects.Composite(
            Effects.AddManaOfChoice(),
            Effects.AddCounters(Counters.UNLOCK, 1, EffectTarget.Self),
        )
        description = "Add one mana of any color. Put an unlock counter on this artifact."
    }

    activatedAbility {
        cost = Costs.SacrificeSelf
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.SourceCounterCountAtLeast(Counters.UNLOCK, 5)
            )
        )
        effect = Effects.Composite(
            Effects.Surveil(3),
            Effects.DrawCards(3),
        )
        description = "Surveil 3, then draw three cards."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "251"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f92a2563-6cfb-4d12-9513-b44d1a7a20ab.jpg?1783912829"

        ruling(
            "2024-02-02",
            "Cryptex's first ability is a mana ability. It doesn't use the stack and can't be " +
                "responded to."
        )
        ruling(
            "2024-02-02",
            "If you can't exile enough cards to meet or exceed the required mana value, you can't " +
                "choose to collect evidence at all."
        )
    }
}
