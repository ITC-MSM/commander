package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Hog-Monkey
 * {2}{B}
 * Creature — Boar Monkey
 * 3/2
 *
 * At the beginning of combat on your turn, target creature you control with a +1/+1 counter on it
 * gains menace until end of turn.
 * Exhaust — {5}: Put two +1/+1 counters on this creature. (Activate each exhaust ability only once.)
 *
 * The begin-combat trigger targets a creature you control carrying a +1/+1 counter (so it pairs with
 * the exhaust ability, which puts counters on Hog-Monkey itself) and grants menace until end of turn.
 * The exhaust ability is the set's once-per-object activated ability — `isExhaust = true` renders the
 * "Exhaust — " prefix and desugars to `ActivationRestriction.Once`.
 */
val HogMonkey = card("Hog-Monkey") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Boar Monkey"
    power = 3
    toughness = 2
    oracleText = "At the beginning of combat on your turn, target creature you control with a +1/+1 counter on it gains menace until end of turn. (It can't be blocked except by two or more creatures.)\n" +
        "Exhaust — {5}: Put two +1/+1 counters on this creature. (Activate each exhaust ability only once.)"

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val creature = target(
            "target creature you control with a +1/+1 counter on it",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE)))
        )
        effect = Effects.GrantKeyword(Keyword.MENACE, creature)
        description = "At the beginning of combat on your turn, target creature you control with a +1/+1 counter on it gains menace until end of turn."
    }

    activatedAbility {
        isExhaust = true
        cost = Costs.Mana("{5}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "104"
        artist = "Miho Midorikawa"
        imageUri = "https://cards.scryfall.io/normal/front/4/f/4f442970-5355-4abf-8684-17daaa8e469b.jpg?1764120722"
    }
}
