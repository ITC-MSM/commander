package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Katara, Bending Prodigy
 * {2}{U}
 * Legendary Creature — Human Warrior Ally
 * 2/3
 *
 * At the beginning of your end step, if Katara is tapped, put a +1/+1 counter on her.
 * Waterbend {6}: Draw a card. (While paying a waterbend cost, you can tap your artifacts and
 * creatures to help. Each one pays for {1}.)
 *
 * Implementation notes:
 *  - The end-step trigger is an intervening-"if" [Triggers.YourEndStep] gated on the source
 *    being tapped ([Conditions.SourceIsTapped]); the payoff adds a +1/+1 counter to the source
 *    ([Effects.AddCounters] with [EffectTarget.Self]).
 *  - "Waterbend {6}" is an activated ability whose mana cost carries the waterbend
 *    alternative-cost flag ([com.wingedsheep.sdk.scripting.ActivatedAbility] `hasWaterbend`);
 *    the reminder text (tap artifacts/creatures to pay {1} each) is supplied by the flag.
 */
val KataraBendingProdigy = card("Katara, Bending Prodigy") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Warrior Ally"
    oracleText = "At the beginning of your end step, if Katara is tapped, put a +1/+1 counter " +
        "on her.\n" +
        "Waterbend {6}: Draw a card. (While paying a waterbend cost, you can tap your " +
        "artifacts and creatures to help. Each one pays for {1}.)"
    power = 2
    toughness = 3

    // At the beginning of your end step, if Katara is tapped, put a +1/+1 counter on her.
    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.SourceIsTapped
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    // Waterbend {6}: Draw a card.
    activatedAbility {
        cost = Costs.Mana("{6}")
        hasWaterbend = true
        effect = DrawCardsEffect(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Mephisto"
        flavorText = "\"You can't knock me down!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8372167-383f-4302-a8ea-b6bf495c870c.jpg?1764120318"
    }
}
