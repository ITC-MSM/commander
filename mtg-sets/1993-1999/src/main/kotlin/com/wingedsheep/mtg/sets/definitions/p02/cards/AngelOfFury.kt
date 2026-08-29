package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Angel of Fury
 * {4}{W}{W}
 * Creature — Angel
 *
 * The dies trigger reads the source itself, so the shuffle is aimed at [EffectTarget.Self];
 * [Effects.ShuffleIntoLibrary] is the library-placement facade for that move.
 */
val AngelOfFury = card("Angel of Fury") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    oracleText =
        "Flying\n" +
        "When this creature dies, you may shuffle it into its owner's library."
    power = 3
    toughness = 5
    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Dies
        effect = MayEffect(Effects.ShuffleIntoLibrary(EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "7"
        artist = "Keith Parkinson"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/2461bf0b-53e4-4103-8485-88a940ad66fd.jpg"
    }
}
