package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Scolding Administrator — Secrets of Strixhaven #224
 * {W}{B} · Creature — Dwarf Cleric · 2/2
 *
 * Menace
 * Repartee — Whenever you cast an instant or sorcery spell that targets a creature, put a +1/+1
 *   counter on this creature.
 * When this creature dies, if it had counters on it, put those counters on up to one target
 *   creature.
 *
 * "Repartee" is an ability word (flavor only) shared with the other SOS Repartee cards — the
 * standard `youCastSpell(InstantOrSorcery)` narrowed by `targetsMatching(Creature)`. The dies
 * trigger is the Essence Channeler shape ([Effects.MoveAllLastKnownCounters] moves every counter
 * kind, not just +1/+1), gated by an intervening "if it had counters on it"
 * ([Conditions.TriggeringEntityHadCounters]) and targeting "up to one target creature"
 * (`TargetCreature(optional = true)`).
 */
val ScoldingAdministrator = card("Scolding Administrator") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Dwarf Cleric"
    power = 2
    toughness = 2
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Repartee — Whenever you cast an instant or sorcery spell that targets a creature, put a +1/+1 counter on this creature.\n" +
        "When this creature dies, if it had counters on it, put those counters on up to one target creature."

    keywords(Keyword.MENACE)

    // Repartee — cast an instant or sorcery targeting a creature: +1/+1 counter on this creature.
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.InstantOrSorcery.targetsMatching(GameObjectFilter.Creature)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Repartee — Whenever you cast an instant or sorcery spell that targets a creature, put a +1/+1 counter on this creature."
    }

    // When this creature dies, if it had counters on it, move those counters to up to one target creature.
    triggeredAbility {
        trigger = Triggers.Dies
        triggerCondition = Conditions.TriggeringEntityHadCounters
        target = TargetCreature(optional = true)
        effect = Effects.MoveAllLastKnownCounters(EffectTarget.ContextTarget(0))
        description = "When this creature dies, if it had counters on it, put those counters on up to one target creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Aleksi Briclot"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69757177-aefa-44a6-81db-5ae9b5d2f117.jpg?1775938562"
    }
}
