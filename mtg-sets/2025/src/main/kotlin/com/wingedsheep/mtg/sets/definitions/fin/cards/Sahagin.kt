package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sahagin
 * {1}{U}
 * Creature — Merfolk Warrior
 * 1/3
 * Whenever you cast a noncreature spell, if at least four mana was spent to cast it, put a
 * +1/+1 counter on this creature and it can't be blocked this turn.
 *
 * "If at least four mana was spent to cast it" is an intervening-if on the cast trigger,
 * modeled by the new [Conditions.TriggeringSpellManaSpentAtLeast] reading the triggering
 * spell's recorded total mana paid (so {X} spells that paid four or more qualify, while a
 * four-mana-value spell cast for less does not). The payoff adds a +1/+1 counter to this
 * creature and grants it CANT_BE_BLOCKED for the turn.
 */
val Sahagin = card("Sahagin") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Warrior"
    power = 1
    toughness = 3
    oracleText = "Whenever you cast a noncreature spell, if at least four mana was spent to cast it, " +
        "put a +1/+1 counter on this creature and it can't be blocked this turn."

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        triggerCondition = Conditions.TriggeringSpellManaSpentAtLeast(4)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            .then(Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Nino Is"
        flavorText = "\"Pshhh... Shhhilence, shhhorewalker! Try to eshhhcape, and I'll shhhlice gills into your neck!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/1/516940c7-c271-4f64-af75-c7ba98548382.jpg?1748706022"
    }
}
