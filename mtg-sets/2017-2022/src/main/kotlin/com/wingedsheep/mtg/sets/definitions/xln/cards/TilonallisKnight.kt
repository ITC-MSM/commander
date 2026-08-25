package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tilonalli's Knight
 * {1}{R}
 * Creature — Human Knight
 * 2/2
 *
 * Whenever this creature attacks, if you control a Dinosaur, this creature gets +1/+1 until end
 * of turn.
 */
val TilonallisKnight = card("Tilonalli's Knight") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Knight"
    oracleText = "Whenever this creature attacks, if you control a Dinosaur, this creature gets " +
        "+1/+1 until end of turn."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.Attacks
        interveningIf = Conditions.ControlPermanentOfType(Subtype.DINOSAUR)
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Lius Lasahido"
        flavorText = "The people of the Sun Empire worship the sun in three aspects. Tilonalli is the Burning Sun, associated with ferocity, fire, and passion."
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b25c2f33-1707-4ff3-a22e-58f51936b733.jpg"
    }
}
