package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Grimgrin, Corpse-Born
 * {3}{U}{B}
 * Legendary Creature — Zombie Warrior
 * 5/5
 * Grimgrin, Corpse-Born enters tapped and doesn't untap during your untap step.
 * Sacrifice another creature: Untap Grimgrin, Corpse-Born and put a +1/+1 counter on it.
 * Whenever Grimgrin, Corpse-Born attacks, destroy target creature defending player controls,
 * then put a +1/+1 counter on Grimgrin, Corpse-Born.
 *
 * "Doesn't untap during your untap step" is the [AbilityFlag.DOESNT_UNTAP] self-suppression flag
 * (cf. Colossus of Sardia); it enters tapped via the [EntersTapped] replacement.
 */
val GrimgrinCorpseBorn = card("Grimgrin, Corpse-Born") {
    manaCost = "{3}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Legendary Creature — Zombie Warrior"
    oracleText = "Grimgrin enters tapped and doesn't untap during your untap step.\n" +
        "Sacrifice another creature: Untap Grimgrin and put a +1/+1 counter on it.\n" +
        "Whenever Grimgrin attacks, destroy target creature defending player controls, then put a +1/+1 counter on Grimgrin."
    power = 5
    toughness = 5

    replacementEffect(EntersTapped())
    flags(AbilityFlag.DOESNT_UNTAP)

    activatedAbility {
        cost = Costs.SacrificeAnother(GameObjectFilter.Creature)
        effect = Effects.Composite(
            listOf(
                Effects.Untap(EffectTarget.Self),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
        )
        description = "Sacrifice another creature: Untap Grimgrin and put a +1/+1 counter on it."
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target("creature defending player controls", Targets.CreatureOpponentControls)
        effect = Effects.Composite(
            listOf(
                Effects.Destroy(creature),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "214"
        artist = "Peter Mohrbacher"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8648734-ed6c-471f-91a1-6b710bbaf370.jpg?1782714696"
    }
}
