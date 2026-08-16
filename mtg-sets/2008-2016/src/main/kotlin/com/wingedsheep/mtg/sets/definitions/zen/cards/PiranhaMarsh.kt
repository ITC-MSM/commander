package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Piranha Marsh
 * Land
 * This land enters tapped.
 * When this land enters, target player loses 1 life.
 * {T}: Add {B}.
 */
val PiranhaMarsh = card("Piranha Marsh") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Land"
    oracleText = "This land enters tapped.\nWhen this land enters, target player loses 1 life.\n{T}: Add {B}."

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val player = target("target player", Targets.Player)
        effect = Effects.LoseLife(1, player)
        description = "When this land enters, target player loses 1 life."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "222"
        artist = "Nic Klein"
        imageUri = "https://cards.scryfall.io/normal/front/e/a/ea077cff-b5c9-4a40-8e66-8810c37be5cb.jpg?1783942122"
    }
}
