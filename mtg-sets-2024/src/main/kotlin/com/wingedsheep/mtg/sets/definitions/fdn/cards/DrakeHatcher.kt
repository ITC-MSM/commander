package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Drake Hatcher
 * {1}{U}
 * Creature — Human Wizard
 * 1/3
 *
 * Vigilance, prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)
 * Whenever this creature deals combat damage to a player, put that many incubation counters on it.
 * Remove three incubation counters from this creature: Create a 2/2 blue Drake creature token with flying.
 *
 * "Incubation" counters here are a card-specific resource counter ([Counters.INCUBATION]) — no
 * inherent rule; the combat-damage trigger accumulates them ("that many" = the damage just dealt,
 * read via [ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT]) and the activated ability spends three to
 * hatch a Drake. NOT MTG's Incubate/incubator-token mechanic.
 */
val DrakeHatcher = card("Drake Hatcher") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    oracleText = "Vigilance, prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "Whenever this creature deals combat damage to a player, put that many incubation counters on it.\n" +
        "Remove three incubation counters from this creature: Create a 2/2 blue Drake creature token with flying."
    power = 1
    toughness = 3

    keywords(Keyword.VIGILANCE)
    prowess()

    // Whenever this creature deals combat damage to a player, put that many incubation counters on it.
    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.AddDynamicCounters(
            Counters.INCUBATION,
            DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_DAMAGE_AMOUNT),
            EffectTarget.Self
        )
    }

    // Remove three incubation counters from this creature: Create a 2/2 blue Drake creature token with flying.
    activatedAbility {
        cost = Costs.RemoveCounterFromSelf(Counters.INCUBATION, 3)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Drake"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/f/4/f4a73034-e20f-4e7e-ac15-3460b1e9c69b.jpg?1782727481"
        )
        description = "Remove three incubation counters: Create a 2/2 blue Drake creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bcaf4196-6bf3-47fa-b5c7-0e77f45cf820.jpg?1782689235"
    }
}
