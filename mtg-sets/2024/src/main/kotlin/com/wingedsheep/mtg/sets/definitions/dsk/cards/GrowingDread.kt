package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Growing Dread
 * {G}{U}
 * Enchantment
 * Flash
 * When this enchantment enters, manifest dread. (Look at the top two cards of your library. Put
 * one onto the battlefield face down as a 2/2 creature and the other into your graveyard. Turn it
 * face up any time for its mana cost if it's a creature card.)
 * Whenever you turn a permanent face up, put a +1/+1 counter on it.
 *
 * The enters trigger reuses the shared [Patterns.Library.manifestDread] recipe. The face-up
 * payoff is the standard [Triggers.CreatureTurnedFaceUp] (any face-up turn you control — in DSK
 * every face-up permanent is a manifested creature) putting a +1/+1 counter on the triggering
 * permanent via [EffectTarget.TriggeringEntity].
 */
val GrowingDread = card("Growing Dread") {
    manaCost = "{G}{U}"
    colorIdentity = "UG"
    typeLine = "Enchantment"
    oracleText = "Flash\nWhen this enchantment enters, manifest dread. (Look at the top two cards " +
        "of your library. Put one onto the battlefield face down as a 2/2 creature and the other " +
        "into your graveyard. Turn it face up any time for its mana cost if it's a creature " +
        "card.)\nWhenever you turn a permanent face up, put a +1/+1 counter on it."

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.manifestDread()
    }

    triggeredAbility {
        trigger = Triggers.CreatureTurnedFaceUp()
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "216"
        artist = "Maxime Minard"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/5479ac50-8335-4c6a-be99-750a44e24f25.jpg?1726286675"
    }
}
