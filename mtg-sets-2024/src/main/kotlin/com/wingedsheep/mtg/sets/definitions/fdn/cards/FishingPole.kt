package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.AbilityId
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.SuccessCriterion
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fishing Pole
 * {1}
 * Artifact — Equipment
 *
 * Equipped creature has "{1}, {T}, Tap Fishing Pole: Put a bait counter on Fishing Pole."
 * Whenever equipped creature becomes untapped, remove a bait counter from this Equipment.
 * If you do, create a 1/1 blue Fish creature token.
 * Equip {2}
 *
 * Three different objects are involved in the granted ability, which is why it needs the
 * granter-scoped cost and target rather than plain `Self`:
 *  - `{T}` taps the **equipped creature** (the ability's host, [Costs.Tap]),
 *  - "Tap Fishing Pole" taps the **Equipment** ([Costs.TapGrantingPermanent]),
 *  - "Put a bait counter on Fishing Pole" puts them on the **Equipment**
 *    ([EffectTarget.GrantingSource]).
 *
 * Per the printed ruling (below) each of those names the *granting* Fishing Pole specifically
 * (CR 201.5a), so a second Fishing Pole on the battlefield is untouched — which is exactly what
 * the granter-relative cost and target give, as opposed to a name/type filter.
 *
 * The counters are spent by an ATTACHED-bound "becomes untapped" trigger. Tapping the Equipment
 * to bait it means the pole can only be baited once per untap cycle, and the trigger's
 * [IfYouDoEffect] correctly makes no Fish when there is no bait counter to remove.
 */
val FishingPole = card("Fishing Pole") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature has \"{1}, {T}, Tap Fishing Pole: Put a bait counter on " +
        "Fishing Pole.\"\n" +
        "Whenever equipped creature becomes untapped, remove a bait counter from this Equipment. " +
        "If you do, create a 1/1 blue Fish creature token.\n" +
        "Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                id = AbilityId.generate(),
                cost = Costs.Composite(
                    Costs.Mana("{1}"),
                    Costs.Tap,
                    Costs.TapGrantingPermanent,
                ),
                effect = AddCountersEffect(Counters.BAIT, 1, EffectTarget.GrantingSource),
            )
            // filter defaults to GroupFilter.attachedCreature() — "equipped creature has ..."
        )
    }

    triggeredAbility {
        trigger = Triggers.becomesUntapped(binding = TriggerBinding.ATTACHED)
        effect = IfYouDoEffect(
            action = Effects.RemoveCounters(Counters.BAIT, 1, EffectTarget.Self),
            // A counter removal is not a zone move, so Auto can't infer it — and "if you do" here
            // really can fail: no bait counter means no Fish.
            successCriterion = SuccessCriterion.CountersRemoved,
            ifYouDo = Effects.CreateToken(
                power = 1,
                toughness = 1,
                colors = setOf(Color.BLUE),
                creatureTypes = setOf(Subtype.FISH.value),
                imageUri = "https://cards.scryfall.io/normal/front/4/4/44057462-40b7-4709-afac-2ce03d53d525.jpg?1783908592",
            ),
        )
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "128"
        artist = "Franz Vohwinkel"
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c95ab836-3277-4223-9aaa-ef2c77256b65.jpg?1783909089"
        ruling(
            "2024-11-08",
            "The ability granted by Fishing Pole's first ability refers only to the Fishing Pole " +
                "granting that ability, not any other permanent on the battlefield named Fishing Pole."
        )
    }
}
