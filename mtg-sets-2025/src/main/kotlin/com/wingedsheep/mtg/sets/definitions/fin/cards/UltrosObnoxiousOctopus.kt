package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ultros, Obnoxious Octopus
 * {1}{U}
 * Legendary Creature — Octopus
 * 2/1
 * Whenever you cast a noncreature spell, if at least four mana was spent to cast it, tap
 *   target creature an opponent controls and put a stun counter on it.
 * Whenever you cast a noncreature spell, if at least eight mana was spent to cast it, put
 *   eight +1/+1 counters on Ultros.
 *
 * Two independent cast triggers, each an intervening-"if" on the mana actually paid for the
 * triggering spell ([Conditions.TriggeringSpellManaSpentAtLeast] — same primitive as Sahagin,
 * so an {X} spell paying the threshold qualifies while a lower-cost spell with a high mana
 * value does not). Both can fire off one eight-mana spell. Per Scryfall ruling, each ability
 * resolves before the triggering spell, and still resolves even if that spell is countered.
 *   - ≥4: tap [Targets.CreatureOpponentControls] and add a stun counter (CR 122 / 701 stun).
 *   - ≥8: add eight +1/+1 counters to Ultros ([EffectTarget.Self]); non-targeting.
 */
val UltrosObnoxiousOctopus = card("Ultros, Obnoxious Octopus") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Octopus"
    power = 2
    toughness = 1
    oracleText = "Whenever you cast a noncreature spell, if at least four mana was spent to cast it, " +
        "tap target creature an opponent controls and put a stun counter on it. (If a permanent with " +
        "a stun counter would become untapped, remove one from it instead.)\n" +
        "Whenever you cast a noncreature spell, if at least eight mana was spent to cast it, put eight " +
        "+1/+1 counters on Ultros."

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        triggerCondition = Conditions.TriggeringSpellManaSpentAtLeast(4)
        val t = target("target", Targets.CreatureOpponentControls)
        effect = Effects.Composite(
            Effects.Tap(t),
            Effects.AddCounters(Counters.STUN, 1, t),
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        triggerCondition = Conditions.TriggeringSpellManaSpentAtLeast(8)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 8, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "83"
        artist = "Domenico Cava"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/14379198-9a0a-4853-9d51-fb074a24b1c0.jpg?1748706073"
    }
}
