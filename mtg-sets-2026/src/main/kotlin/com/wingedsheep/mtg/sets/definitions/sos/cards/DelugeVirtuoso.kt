package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.opus
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Deluge Virtuoso
 * {2}{U}
 * Creature — Human Wizard
 * 2/2
 *
 * When this creature enters, tap target creature an opponent controls and put a stun counter on it.
 * (If a permanent with a stun counter would become untapped, remove one from it instead.)
 *
 * Opus — Whenever you cast an instant or sorcery spell, this creature gets +1/+1 until end of turn.
 * If five or more mana was spent to cast that spell, this creature gets +2/+2 until end of turn instead.
 *
 * The ETB taps + stun-counters a single chosen opponent creature (tap then add a "STUN" counter on the
 * same `ContextTarget(0)`). "Opus" is an ability word (flavor only); the `opus { }` builder wires the
 * spell-cast trigger and the 5+ mana tier. Here the 5+ mana bonus **replaces** the base pump
 * (`insteadIfFiveOrMore`): +1/+1 normally, +2/+2 when five or more mana was spent. `ModifyStats`
 * defaults to an until-end-of-turn buff.
 */
val DelugeVirtuoso = card("Deluge Virtuoso") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, tap target creature an opponent controls and put a stun " +
        "counter on it. (If a permanent with a stun counter would become untapped, remove one from it " +
        "instead.)\nOpus — Whenever you cast an instant or sorcery spell, this creature gets +1/+1 " +
        "until end of turn. If five or more mana was spent to cast that spell, this creature gets " +
        "+2/+2 until end of turn instead."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = Targets.CreatureOpponentControls
        effect = Effects.Tap(EffectTarget.ContextTarget(0))
            .then(Effects.AddCounters(Counters.STUN, 1, EffectTarget.ContextTarget(0)))
    }

    opus {
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        insteadIfFiveOrMore = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Opus — Whenever you cast an instant or sorcery spell, this creature gets +1/+1 " +
            "until end of turn. If five or more mana was spent to cast that spell, this creature gets " +
            "+2/+2 until end of turn instead."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "42"
        artist = "Justine Cruz"
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e3b16ed-8727-48fd-8b1f-c0cbd329385e.jpg?1775937202"
    }
}
