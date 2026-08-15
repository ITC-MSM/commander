package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Thistledown Players
 * {2}{W}
 * Creature — Mouse Bard
 * 3/3
 *
 * Whenever this creature attacks, untap target nonland permanent.
 */
val ThistledownPlayers = card("Thistledown Players") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Mouse Bard"
    power = 3
    toughness = 3
    oracleText = "Whenever this creature attacks, untap target nonland permanent."

    triggeredAbility {
        trigger = Triggers.Attacks
        val nonland = target("nonland permanent", Targets.NonlandPermanent)
        effect = TapUntapEffect(nonland, tap = false)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "John Thacker"
        flavorText = "\"In hand, a sword to slay the frightful beast; in heart, these words to soothe our frightened kin.\"\n—Lily and the Ember Seed"
        imageUri = "https://cards.scryfall.io/normal/front/a/f/afa8d83f-8586-4127-8b55-9715e9547488.jpg?1721425974"
    }
}
