package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.SelectTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Airbender Ascension — {1}{W} Enchantment
 *
 * When this enchantment enters, airbend up to one target creature.
 * Whenever a creature you control enters, put a quest counter on this enchantment.
 * At the beginning of your end step, if this enchantment has four or more quest counters on it,
 * exile up to one target creature you control, then return it to the battlefield under its
 * owner's control.
 *
 * Modeling notes (same Ascension cycle as Earthbender/Waterbender Ascension):
 *  - The counter trigger is [Triggers.OtherCreatureEnters] (filter = creatures you control, OTHER
 *    binding) — for an enchantment source every creature you control is "other", so it reads as
 *    "whenever a creature you control enters".
 *  - The end-step ability is an intervening-"if" (CR 603.4): gated by
 *    [Conditions.SourceCounterCountAtLeast]`(QUEST, 4)`, and the "up to one target creature you
 *    control" is chosen at resolution via [SelectTargetEffect] *inside* the satisfied gate, so no
 *    creature is picked when below the threshold. The blink is exile → return (Splash Portal shape),
 *    which re-enters the creature under its owner's control.
 */
val AirbenderAscension = card("Airbender Ascension") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, airbend up to one target creature.\n" +
        "Whenever a creature you control enters, put a quest counter on this enchantment.\n" +
        "At the beginning of your end step, if this enchantment has four or more quest counters on it, exile up to one target creature you control, then return it to the battlefield under its owner's control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target("up to one target creature", Targets.UpToCreatures(1))
        effect = Effects.Airbend()
    }

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.AddCounters(Counters.QUEST, 1, EffectTarget.Self)
        description = "Whenever a creature you control enters, put a quest counter on this enchantment."
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = ConditionalEffect(
            condition = Conditions.SourceCounterCountAtLeast(Counters.QUEST, 4),
            effect = Effects.Composite(
                SelectTargetEffect(
                    requirement = TargetObject(filter = TargetFilter.CreatureYouControl, optional = true),
                    storeAs = "flickered"
                ),
                Effects.Move(EffectTarget.PipelineTarget("flickered"), Zone.EXILE),
                Effects.Move(EffectTarget.PipelineTarget("flickered"), Zone.BATTLEFIELD)
            )
        )
        description = "At the beginning of your end step, if this enchantment has four or more quest counters on it, exile up to one target creature you control, then return it to the battlefield under its owner's control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Shiren"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99a90d13-891c-45cc-b1d5-6080ebae5862.jpg?1764119907"
    }
}
