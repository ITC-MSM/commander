package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Vona, Butcher of Magan
 * {3}{W}{B}
 * Legendary Creature — Vampire Knight
 * 4/4
 *
 * Vigilance, lifelink
 * {T}, Pay 7 life: Destroy target nonland permanent. Activate only during your turn.
 */
val VonaButcherOfMagan = card("Vona, Butcher of Magan") {
    manaCost = "{3}{W}{B}"
    colorIdentity = "BW"
    typeLine = "Legendary Creature — Vampire Knight"
    oracleText = "Vigilance, lifelink\n" +
        "{T}, Pay 7 life: Destroy target nonland permanent. Activate only during your turn."
    power = 4
    toughness = 4

    keywords(Keyword.VIGILANCE, Keyword.LIFELINK)

    activatedAbility {
        cost = Costs.Composite(Costs.Tap, Costs.PayLife(7))
        val victim = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Nonland and GameObjectFilter.Permanent))
        )
        effect = Effects.Destroy(victim)
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "231"
        artist = "Volkan Baǵa"
        flavorText = "\"For four hundred years, I have led armies of conquest. These lands hold nothing that can stand against me.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1fd4101-d531-43aa-9cb6-79e36a8121d8.jpg"
    }
}
