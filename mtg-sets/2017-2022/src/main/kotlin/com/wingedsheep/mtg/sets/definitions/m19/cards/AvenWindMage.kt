package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Aven Wind Mage
 * {2}{U}
 * Creature — Bird Wizard
 * 2/2
 * Flying
 * Whenever you cast an instant or sorcery spell, this creature gets +1/+1 until end of turn.
 */
val AvenWindMage = card("Aven Wind Mage") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Wizard"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Whenever you cast an instant or sorcery spell, this creature gets +1/+1 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Lius Lasahido"
        flavorText = "\"My skill sharpens with each beat of my wings.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b12dfc1-81f2-44b2-baa2-73cc21363978.jpg"
    }
}
