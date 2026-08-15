package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zack Fair
 * {W}
 * Legendary Creature — Human Soldier
 * 0/1
 *
 * Zack Fair enters with a +1/+1 counter on it.
 * {1}, Sacrifice Zack Fair: Target creature you control gains indestructible until end of turn.
 * Put Zack Fair's counters on that creature and attach an Equipment that was attached to Zack Fair
 * to that creature.
 *
 * Implementation notes — Zack is sacrificed as part of the activation *cost*, so by the time the
 * ability resolves Zack has left the battlefield. Both halves of the resolution therefore read
 * last-known information (CR 112.7a) captured before the cost was paid:
 *
 *  - **Counters** — [Effects.MoveAllLastKnownCounters] moves *every* counter kind Zack had (per the
 *    Bloomburrow/Essence Channeler ruling, and the Zack Fair ruling that it puts the same number of
 *    each kind, not just +1/+1). The executor falls back to the cost-sacrifice last-known map when
 *    there is no dies/leaves trigger.
 *  - **Equipment** — [CardSource.LastKnownEquipmentAttachedToSource] gathers the Equipment that was
 *    attached to Zack (still on the battlefield and still Equipment); [chooseExactly] picks one when
 *    more than one qualifies (auto-resolves with one, no-op with none), then
 *    [Effects.AttachTargetEquipmentToCreature] re-attaches the chosen one to the target creature.
 *
 * Edge cases: no counters → the counter move is a no-op; no qualifying Equipment → the gather is
 * empty, the select stores nothing, and the attach no-ops; the target becoming illegal before
 * resolution fizzles the whole ability (CR 608.2c) since it's the single target.
 */
val ZackFair = card("Zack Fair") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Soldier"
    power = 0
    toughness = 1
    oracleText = "Zack Fair enters with a +1/+1 counter on it.\n" +
        "{1}, Sacrifice Zack Fair: Target creature you control gains indestructible until end of " +
        "turn. Put Zack Fair's counters on that creature and attach an Equipment that was attached " +
        "to Zack Fair to that creature."

    // Zack Fair enters with a +1/+1 counter on it.
    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.PlusOnePlusOne,
            count = 1,
            selfOnly = true
        )
    )

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        val creature = target("creature you control", Targets.CreatureYouControl)
        effect = Effects.Pipeline {
            // Target creature gains indestructible until end of turn.
            run(Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature, Duration.EndOfTurn))
            // Put Zack Fair's counters (every kind) on that creature.
            run(Effects.MoveAllLastKnownCounters(creature))
            // Attach an Equipment that was attached to Zack Fair to that creature (player chooses
            // which when more than one qualifies).
            val equipment = gather(CardSource.LastKnownEquipmentAttachedToSource)
            val chosen = chooseExactly(
                1,
                from = equipment,
                useTargetingUI = true,
                prompt = "Choose an Equipment that was attached to Zack Fair to attach"
            )
            run(
                Effects.AttachTargetEquipmentToCreature(
                    equipmentTarget = EffectTarget.PipelineTarget(chosen.key, 0),
                    creatureTarget = creature
                )
            )
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Yoshio Sugiura"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f21f9161-5945-40da-8da0-446f6a4a1c23.jpg?1748705924"

        ruling("2025-06-06", "Zack Fair's last ability puts all counters that were on Zack Fair onto the target creature, not just its +1/+1 counters.")
        ruling("2025-06-06", "Zack Fair's last ability doesn't cause you to move counters from Zack Fair onto the target creature. Rather, you put the same number of each kind of counter Zack Fair had when it was sacrificed onto the target creature.")
        ruling("2025-06-06", "In some unusual cases, you may end up putting the appropriate counters on more than one permanent. For example, if you control The Ozolith when Zack Fair's last ability resolves, you'll put the appropriate counters onto both The Ozolith and the target creature.")
    }
}
