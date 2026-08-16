package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dragon's Hoard
 * {3}
 * Artifact
 * Whenever a Dragon you control enters, put a gold counter on this artifact.
 * {T}, Remove a gold counter from this artifact: Draw a card.
 * {T}: Add one mana of any color.
 *
 * Same shape as Bandit's Haul: a counter-accruing trigger plus two activated abilities — a
 * tap-and-spend draw and a plain any-color mana ability.
 */
val DragonsHoard = card("Dragon's Hoard") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a Dragon you control enters, put a gold counter on this artifact.\n{T}, Remove a gold counter from this artifact: Draw a card.\n{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.youControl().withSubtype(Subtype.DRAGON),
            binding = TriggerBinding.ANY
        )
        effect = Effects.AddCounters(Counters.GOLD, 1, EffectTarget.Self)
        description = "Whenever a Dragon you control enters, put a gold counter on this artifact."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.GOLD, 1)
        )
        effect = Effects.DrawCards(1)
        description = "{T}, Remove a gold counter from this artifact: Draw a card."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add one mana of any color."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "232"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b441fc8-bc89-47d4-8745-2525aeb6d98d.jpg?1783934514"
    }
}
