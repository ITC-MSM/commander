package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.AddManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wandertale Mentor {R}{G}
 * Creature — Raccoon Bard
 * 2/2
 *
 * Whenever you expend 4, put a +1/+1 counter on this creature.
 * {T}: Add {R} or {G}.
 */
val WandertaleMentor = card("Wandertale Mentor") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Raccoon Bard"
    power = 2
    toughness = 2
    oracleText = "Whenever you expend 4, put a +1/+1 counter on this creature. (You expend 4 as you spend your fourth total mana to cast spells during a turn.)\n{T}: Add {R} or {G}."

    triggeredAbility {
        trigger = Triggers.Expend(4)
        effect = Effects.AddCounters("+1/+1", 1, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = AddManaEffect(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "240"
        artist = "Jakub Kasper"
        flavorText = "\"You've all grown so much since I passed through this burrow last! Let's see, where to begin this story?\""
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8c399a55-d02e-41ed-b827-8784b738c118.jpg?1721427242"
    }
}
