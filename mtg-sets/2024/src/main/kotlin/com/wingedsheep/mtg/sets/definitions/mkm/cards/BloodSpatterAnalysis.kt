package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Blood Spatter Analysis — Murders at Karlov Manor #189
 * {B}{R} · Enchantment · Rare
 *
 * When this enchantment enters, it deals 3 damage to target creature an opponent controls.
 * Whenever one or more creatures die, mill a card and put a bloodstain counter on this enchantment.
 * Then sacrifice it if it has five or more bloodstain counters on it. When you do, return target
 * creature card from your graveyard to your hand.
 *
 * Two mana for a Shock stapled to a five-turn fuse: the enchantment counts deaths, self-mills toward
 * the payoff it will eventually buy back, and when the fifth bloodstain lands it sacrifices itself
 * and Raises Dead. It is the set's crime-scene enchantment in mechanical form — the evidence piles
 * up until it points at a body.
 *
 * **The death trigger is batched.** [Triggers.OneOrMoreCreaturesDie] fires at most once per death
 * batch regardless of how many creatures died simultaneously or who controlled them (CR 603.3b), so
 * a board wipe advances the fuse by exactly one, not by six. Same shape as DSK's Chainsaw.
 *
 * **The sacrifice is not a state-based action.** The ruling below is explicit: the enchantment is
 * sacrificed for having five counters *only while its second ability is resolving*. Get a fifth
 * bloodstain counter onto it some other way (proliferate, a counter-doubler) and nothing happens
 * until the next death batch. That is why the threshold lives inside the trigger's effect as a
 * [ConditionalEffect] over [Conditions.SourceCounterCountAtLeast] rather than as a
 * state-trigger/SBA — the check is a step in the resolution, not a continuous one.
 *
 * **"When you do" is a genuine reflexive trigger** (CR 603.12), not an inline continuation, so the
 * creature card is targeted as the reflexive ability goes on the stack — *after* the sacrifice — and
 * opponents get a real priority window to respond and make that target illegal. That is
 * [ReflexiveTriggerEffect] with `optional = false`: the sacrifice isn't a choice once the condition
 * is met, but the payoff still needs the stack round-trip. Wiring it as another `.then(…)` step
 * would wrongly force the target to be chosen up front and deny the response window, which the
 * second ruling below calls out by name.
 *
 * The bloodstain counter is a passive storage counter with no inherent rule
 * ([Counters.BLOODSTAIN]) — the card's own trigger both writes and reads it.
 */
val BloodSpatterAnalysis = card("Blood Spatter Analysis") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, it deals 3 damage to target creature an opponent " +
        "controls.\n" +
        "Whenever one or more creatures die, mill a card and put a bloodstain counter on this " +
        "enchantment. Then sacrifice it if it has five or more bloodstain counters on it. When you " +
        "do, return target creature card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target(
            "target creature an opponent controls",
            TargetCreature(filter = TargetFilter.Creature.opponentControls())
        )
        effect = Effects.DealDamage(3, victim)
        description = "When this enchantment enters, it deals 3 damage to target creature an " +
            "opponent controls."
    }

    triggeredAbility {
        trigger = Triggers.OneOrMoreCreaturesDie()
        effect = Patterns.Library.mill(1)
            .then(Effects.AddCounters(Counters.BLOODSTAIN, 1, EffectTarget.Self))
            .then(
                ConditionalEffect(
                    condition = Conditions.SourceCounterCountAtLeast(Counters.BLOODSTAIN, 5),
                    effect = ReflexiveTriggerEffect(
                        action = Effects.SacrificeTarget(EffectTarget.Self),
                        optional = false,
                        reflexiveEffect = Effects.ReturnToHand(EffectTarget.ContextTarget(0)),
                        reflexiveTargetRequirements = listOf(
                            TargetObject(
                                filter = TargetFilter(
                                    baseFilter = GameObjectFilter.Creature.ownedByYou(),
                                    zone = Zone.GRAVEYARD
                                )
                            )
                        )
                    )
                )
            )
        description = "Whenever one or more creatures die, mill a card and put a bloodstain " +
            "counter on this enchantment. Then sacrifice it if it has five or more bloodstain " +
            "counters on it. When you do, return target creature card from your graveyard to " +
            "your hand."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "189"
        artist = "Jokubas Uogintas"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2b80feb8-5cc8-4e91-ac22-a733305a67de.jpg?1783912856"

        ruling(
            "2024-02-02",
            "Blood Spatter Analysis is sacrificed for having five or more bloodstain counters on " +
                "it only while its second ability is resolving. If you put a fifth counter on it " +
                "some other way, it won't be immediately sacrificed."
        )
        ruling(
            "2024-02-02",
            "You don't choose a target creature card to return from your graveyard to your hand at " +
                "the time Blood Spatter Analysis's second ability triggers. Rather, a second " +
                "\"reflexive\" ability triggers when you sacrifice Blood Spatter Analysis this " +
                "way. You choose a target for that ability as it goes on the stack. Each player " +
                "may respond to this triggered ability as normal."
        )
        ruling(
            "2024-02-02",
            "Blood Spatter Analysis's reflexive triggered ability triggers and returns a creature " +
                "card to your hand only if you sacrifice it while resolving its second ability. " +
                "It won't trigger if you sacrifice it for any other reason."
        )
    }
}
