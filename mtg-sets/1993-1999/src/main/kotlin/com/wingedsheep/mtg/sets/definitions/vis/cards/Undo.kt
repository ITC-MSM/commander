package com.wingedsheep.mtg.sets.definitions.vis.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Undo
 * {1}{U}{U}
 * Sorcery
 * Return two target creatures to their owners' hands.
 *
 * One requirement with `count = 2` rather than two requirements: the two creatures are chosen
 * together and must be different objects. The bounce runs once per chosen target via
 * [ForEachTargetEffect], whose body reads the current iteration's target as `ContextTarget(0)`.
 */
val Undo = card("Undo") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Return two target creatures to their owners' hands."

    spell {
        target("target", TargetObject(count = 2, filter = TargetFilter.Creature))
        effect = ForEachTargetEffect(
            effects = listOf(Effects.ReturnToHand(EffectTarget.ContextTarget(0)))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Terese Nielsen"
        flavorText = "\"Oft have I wished to undo past deeds, but never did I imagine they would be undone for me.\"\n—Naimah, Femeref philosopher"
        imageUri = "https://cards.scryfall.io/normal/front/2/b/2bef942e-9d17-4d40-a4c9-8be715e73a08.jpg"
    }
}
