package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lightless Evangel
 * {1}{B}
 * Creature — Vampire Cleric
 * Whenever you sacrifice another creature or artifact, put a +1/+1 counter on this creature.
 * 2/2
 */
val LightlessEvangel = card("Lightless Evangel") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Cleric"
    power = 2
    toughness = 2
    oracleText = "Whenever you sacrifice another creature or artifact, put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.YouSacrificeOneOrMore(
            GameObjectFilter.Creature.or(GameObjectFilter.Artifact)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you sacrifice another creature or artifact, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "109"
        artist = "Viko Menezes"
        flavorText = "\"You have been given great purpose. Your matter will feed the singularity.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/8/6860556a-6c34-4c41-89ea-f0bc495a159c.jpg?1752946994"
    }
}
