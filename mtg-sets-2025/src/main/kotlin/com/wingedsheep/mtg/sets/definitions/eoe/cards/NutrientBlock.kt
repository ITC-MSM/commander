package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.*
import com.wingedsheep.sdk.model.Rarity

/**
 * Nutrient Block
 * {1}
 * Artifact — Food
 * Indestructible (Effects that say "destroy" don't destroy this artifact.)
 * {2}, {T}, Sacrifice this artifact: You gain 3 life.
 * When this artifact is put into a graveyard from the battlefield, draw a card.
 */
val NutrientBlock = card("Nutrient Block") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Food"
    oracleText = "Indestructible (Effects that say \"destroy\" don't destroy this artifact.)\n{2}, {T}, Sacrifice this artifact: You gain 3 life.\nWhen this artifact is put into a graveyard from the battlefield, draw a card."

    keywords(Keyword.INDESTRUCTIBLE)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.GainLife(3)
    }

    triggeredAbility {
        trigger = Triggers.PutIntoGraveyardFromBattlefield
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "243"
        artist = "Francisco Miyara"
        flavorText = "\"What flavor do you want, Sami? Bland, dense, or vanilla?\"\n—Tannuk"
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a26064bb-c568-4ed6-86db-3aab69b050db.jpg?1752947549"
    }
}
