package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Anchovy & Banana Pizza
 * {2}{B}{B}
 * Artifact — Food
 *
 * When this artifact enters, destroy target creature.
 * {2}, {T}, Sacrifice this artifact: You gain 3 life.
 */
val AnchovyAndBananaPizza = card("Anchovy & Banana Pizza") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Artifact — Food"
    oracleText = "When this artifact enters, destroy target creature.\n{2}, {T}, Sacrifice this artifact: You gain 3 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val target = target("target creature", TargetCreature())
        effect = Effects.Destroy(target)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "57"
        artist = "Daniel Romanovsky"
        flavorText = "\"Question: Do you like penicillin on your pizza?\"\n—Donatello"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/44443ad2-ef8c-4106-ba1b-ee3fb6fd8b17.jpg?1771586832"
    }
}
