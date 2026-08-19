package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Scroll of Avacyn
 * {1}
 * Artifact
 *
 * {1}, Sacrifice this artifact: Draw a card. If you control an Angel, you gain 5 life.
 */
val ScrollOfAvacyn = card("Scroll of Avacyn") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, Sacrifice this artifact: Draw a card. If you control an Angel, you gain 5 life."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.DrawCards(1).then(
            ConditionalEffect(
                condition = Conditions.ControlPermanentOfType(Subtype.ANGEL),
                effect = Effects.GainLife(5),
            )
        )
        description = "{1}, Sacrifice this artifact: Draw a card. If you control an Angel, you gain 5 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "220"
        artist = "Cliff Childs"
        flavorText = "Words to bless the eye that reads them, telling of a future beyond the reach of fear."
        imageUri = "https://cards.scryfall.io/normal/front/8/7/871e6e2a-7e45-446b-b964-94377eb6ca92.jpg?1783940650"
    }
}
