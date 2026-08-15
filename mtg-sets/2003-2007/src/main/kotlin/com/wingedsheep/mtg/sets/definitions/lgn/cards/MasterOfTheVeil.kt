package com.wingedsheep.mtg.sets.definitions.lgn.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.TurnFaceDownEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Master of the Veil
 * {2}{U}{U}
 * Creature — Human Wizard
 * 2/3
 * Morph {2}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)
 * When this creature is turned face up, you may turn target creature with a morph ability face down.
 */
val MasterOfTheVeil = card("Master of the Veil") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3
    oracleText = "Morph {2}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\nWhen this creature is turned face up, you may turn target creature with a morph ability face down."

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        optional = true
        val t = target("creature with a morph ability", TargetPermanent(
            filter = TargetFilter(GameObjectFilter.Creature.withMorph().faceUp())
        ))
        effect = TurnFaceDownEffect(t)
    }

    morph = "{2}{U}"

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "43"
        artist = "Ron Spears"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7ce1755-9f4a-4741-b6e5-288595ec494d.jpg?1562938335"
    }
}
