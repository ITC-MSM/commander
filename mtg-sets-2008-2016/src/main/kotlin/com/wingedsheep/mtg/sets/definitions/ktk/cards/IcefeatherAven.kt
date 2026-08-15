package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Icefeather Aven
 * {G}{U}
 * Creature — Bird Shaman
 * 2/2
 * Flying
 * Morph {1}{G}{U}
 * When this creature is turned face up, you may return another target creature to its owner's hand.
 */
val IcefeatherAven = card("Icefeather Aven") {
    manaCost = "{G}{U}"
    colorIdentity = "UG"
    typeLine = "Creature — Bird Shaman"
    power = 2
    toughness = 2
    oracleText = "Flying\nMorph {1}{G}{U} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\nWhen Icefeather Aven is turned face up, you may return another target creature to its owner's hand."

    keywords(Keyword.FLYING)

    morph = "{1}{G}{U}"

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        optional = true
        val t = target("another creature", TargetCreature(filter = TargetFilter.OtherCreature))
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "178"
        artist = "Slawomir Maniak"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ecdee089-8e89-4c99-9390-e8f4c189cffb.jpg?1562795587"
        ruling("2014-09-20", "If you control the only other creatures when Icefeather Aven is turned face up, you must target one of them.")
    }
}
