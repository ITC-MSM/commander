package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mesmeric Orb
 * {2}
 * Artifact
 *
 * Whenever a permanent becomes untapped, that permanent's controller mills a card.
 */
val MesmericOrb = card("Mesmeric Orb") {
    manaCost = "{2}"
    typeLine = "Artifact"
    oracleText = "Whenever a permanent becomes untapped, that permanent's controller mills a card."

    triggeredAbility {
        trigger = Triggers.becomesUntapped(binding = TriggerBinding.ANY)
        effect = Patterns.Library.mill(1, EffectTarget.ControllerOfTriggeringEntity)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "204"
        artist = "David Martin"
        flavorText = "A step in one direction is two steps away from another."
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29d11b14-43a9-4d1c-ba2c-3025d51d841e.jpg?1783944514"
        ruling("8/7/2020", "If permanents become untapped during the untap step, Mesmeric Orb's ability triggers once for each of them. The triggers wait to be put on the stack until the upkeep begins.")
    }
}
