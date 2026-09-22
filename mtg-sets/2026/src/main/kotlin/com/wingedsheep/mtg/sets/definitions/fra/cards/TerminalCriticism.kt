package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

val TerminalCriticism = card("Terminal Criticism") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Destroy target creature or planeswalker that's blue or red. You gain 1 life."

    spell {
        val permanent = target("permanent", TargetPermanent(
            filter = TargetFilter(GameObjectFilter.CreatureOrPlaneswalker.withAnyColor(Color.BLUE, Color.RED))
        ))
        effect = Effects.Composite(Effects.Destroy(permanent), Effects.GainLife(1))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "68"
        artist = "Borja Pindado"
        flavorText = "Prismari believed art was in the eye of the beholder. Hexhaven was a harsh critic."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ebd7e38-b27c-4c6e-aaea-e8ee5ba5e5df.jpg?1789470810"
        inBooster = false
    }
}
