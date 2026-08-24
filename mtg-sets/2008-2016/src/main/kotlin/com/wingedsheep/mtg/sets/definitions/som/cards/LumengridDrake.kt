package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Lumengrid Drake
 * {3}{U}
 * Creature — Drake
 * 2/2
 *
 * Flying
 * Metalcraft — When this creature enters, if you control three or more artifacts, return target creature to its owner's hand.
 *
 * Metalcraft is an ability word, not a keyword: it survives only in the printed text, and the rules
 * text is an ordinary intervening-if clause on the enters trigger. As an intervening if it is
 * checked twice (CR 603.4) — once when the Drake enters, and again as the ability resolves — so
 * losing the third artifact in response fizzles the bounce.
 */
val LumengridDrake = card("Lumengrid Drake") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Metalcraft — When this creature enters, if you control three or more artifacts, return target creature to its owner's hand."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact)
        val victim = target("target creature", Targets.Creature)
        effect = Effects.ReturnToHand(victim)
        description = "Metalcraft — When this creature enters, if you control three or more artifacts, " +
            "return target creature to its owner's hand."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "36"
        artist = "Johann Bodin"
        flavorText = "Properly outfitted, drakes made perfect sentries for vedalken strongholds."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f44e9820-2209-40a2-bc4f-46b440c05e9d.jpg?1783941738"
    }
}
