package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jeong Jeong, the Deserter
 * {2}{R}
 * Legendary Creature — Human Rebel Ally
 * 2/3
 *
 * Firebending 1
 * Exhaust — {3}: Put a +1/+1 counter on Jeong Jeong. When you next cast a Lesson spell this turn,
 *   copy it and you may choose new targets for the copy. (Activate each exhaust ability only once.)
 *
 * The exhaust ability composes a +1/+1 counter with [Effects.CopyNextSpellCast] filtered to Lesson
 * spells — a one-shot "copy your next [filter] spell this turn" rider that already offers new targets
 * for the copy.
 */
val JeongJeongTheDeserter = card("Jeong Jeong, the Deserter") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Rebel Ally"
    power = 2
    toughness = 3
    oracleText = "Firebending 1 (Whenever this creature attacks, add {R}. This mana lasts until end of combat.)\n" +
        "Exhaust — {3}: Put a +1/+1 counter on Jeong Jeong. When you next cast a Lesson spell this turn, " +
        "copy it and you may choose new targets for the copy. (Activate each exhaust ability only once.)"

    firebending(1)

    activatedAbility {
        isExhaust = true
        cost = Costs.Mana("{3}")
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            Effects.CopyNextSpellCast(1, GameObjectFilter.Any.withSubtype("Lesson")),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "142"
        artist = "Danciao"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a9f63d3b-bee5-48bc-8b04-d8b24b0bda6e.jpg?1764120974"
    }
}
