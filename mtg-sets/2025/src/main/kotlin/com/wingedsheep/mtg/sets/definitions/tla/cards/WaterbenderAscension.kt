package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.events.DamageType
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Waterbender Ascension
 * {1}{U}
 * Enchantment
 *
 * Whenever a creature you control deals combat damage to a player, put a quest counter on this
 * enchantment. Then if it has four or more quest counters on it, draw a card.
 * Waterbend {4}: Target creature can't be blocked this turn. (While paying a waterbend cost, you
 * can tap your artifacts and creatures to help. Each one pays for {1}.)
 *
 * Modeling notes:
 *  - The combat-damage trigger fires for any creature you control (binding ANY, source-filtered)
 *    via [Triggers.dealsDamage] — same shape as Impostor Syndrome.
 *  - Intervening-"if" payoff (CR 603.4): putting the quest counter is mandatory; only if the
 *    enchantment then has four or more quest counters does the draw happen. The counter add is
 *    sequenced first, then [ConditionalEffect] gates the draw on the live count
 *    (`SourceCounterCountAtLeast`) — mirrors Earthbender Ascension's quest-counter pattern.
 *  - Waterbend is a keyword cost ({4}, payable by tapping your artifacts/creatures) modeled with
 *    `hasWaterbend = true` like Geyser Leaper; the can't-be-blocked grant defaults to end of turn.
 */
val WaterbenderAscension = card("Waterbender Ascension") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever a creature you control deals combat damage to a player, put a quest counter on this enchantment. Then if it has four or more quest counters on it, draw a card.\n" +
        "Waterbend {4}: Target creature can't be blocked this turn. (While paying a waterbend cost, you can tap your artifacts and creatures to help. Each one pays for {1}.)"

    // Whenever a creature you control deals combat damage to a player, put a quest counter on this
    // enchantment. Then if it has four or more quest counters on it, draw a card.
    triggeredAbility {
        trigger = Triggers.dealsDamage(
            damageType = DamageType.Combat,
            recipient = RecipientFilter.AnyPlayer,
            sourceFilter = GameObjectFilter.Creature.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.Composite(
            Effects.AddCounters(Counters.QUEST, 1, EffectTarget.Self),
            ConditionalEffect(
                condition = Conditions.SourceCounterCountAtLeast(Counters.QUEST, 4),
                effect = Effects.DrawCards(1)
            )
        )
        description = "Whenever a creature you control deals combat damage to a player, put a quest counter on this enchantment. Then if it has four or more quest counters on it, draw a card."
    }

    // Waterbend {4}: Target creature can't be blocked this turn.
    activatedAbility {
        cost = Costs.Mana("{4}")
        hasWaterbend = true
        val t = target("target creature", Targets.Creature)
        effect = GrantKeywordEffect(AbilityFlag.CANT_BE_BLOCKED.name, t)
        description = "Waterbend {4}: Target creature can't be blocked this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "79"
        artist = "Takeuchi Moto"
        imageUri = "https://cards.scryfall.io/normal/front/3/f/3f57e0f9-e232-489c-b991-d0d23f75d8dd.jpg?1764120533"
    }
}
