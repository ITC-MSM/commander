package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Recover
 * {2}{B}
 * Sorcery
 * Return target creature card from your graveyard to your hand.
 * Draw a card.
 */
val Recover = card("Recover") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to your hand.\nDraw a card."

    spell {
        target = TargetObject(filter = TargetFilter.CreatureInYourGraveyard)
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Nelson DeCastro"
        flavorText = "As Barrin exhumed his daughter's body, he finally realized the full price of his faith in Urza."
        imageUri = "https://cards.scryfall.io/normal/front/7/7/771e695b-24e1-4c65-81e0-1624bda646e7.jpg?1562918903"
    }
}
