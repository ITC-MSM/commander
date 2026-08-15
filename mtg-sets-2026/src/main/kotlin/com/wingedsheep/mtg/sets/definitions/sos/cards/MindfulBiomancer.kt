package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mindful Biomancer
 * {1}{G}
 * Creature — Dryad Druid, 2/2
 * When this creature enters, you gain 1 life.
 * {2}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn.
 */
val MindfulBiomancer = card("Mindful Biomancer") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad Druid"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you gain 1 life.\n" +
        "{2}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = Costs.Mana("{2}{G}")
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OncePerTurn)
        description = "{2}{G}: This creature gets +2/+2 until end of turn. Activate only once each turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Josu Hernaiz"
        flavorText = "\"I sometimes think I've learned more from the smallest of beings than from all of my professors combined.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2c3a6eb8-ce0c-4dc8-9ed6-d2a9223eef53.jpg?1775938052"
    }
}
