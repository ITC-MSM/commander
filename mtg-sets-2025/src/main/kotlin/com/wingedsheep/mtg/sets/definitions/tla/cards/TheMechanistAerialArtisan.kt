package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * The Mechanist, Aerial Artisan
 * {2}{U}
 * Legendary Creature — Human Artificer Ally
 * 1/3
 *
 * Whenever you cast a noncreature spell, create a Clue token. (It's an artifact with
 * "{2}, Sacrifice this token: Draw a card.")
 * {T}: Until end of turn, target artifact token you control becomes a 3/1 Construct
 * artifact creature with flying.
 */
val TheMechanistAerialArtisan = card("The Mechanist, Aerial Artisan") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Artificer Ally"
    oracleText = "Whenever you cast a noncreature spell, create a Clue token. " +
        "(It's an artifact with \"{2}, Sacrifice this token: Draw a card.\")\n" +
        "{T}: Until end of turn, target artifact token you control becomes a 3/1 Construct " +
        "artifact creature with flying."
    power = 1
    toughness = 3

    // Whenever you cast a noncreature spell, create a Clue token.
    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.CreateClue()
    }

    // {T}: Until end of turn, target artifact token you control becomes a 3/1 Construct
    // artifact creature with flying.
    activatedAbility {
        cost = Costs.Tap
        val artifactToken = target(
            "target artifact token you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact.token().youControl())),
        )
        effect = Effects.BecomeCreature(
            target = artifactToken,
            power = 3,
            toughness = 1,
            keywords = setOf(Keyword.FLYING),
            creatureTypes = setOf("Construct"),
            addTypes = setOf("ARTIFACT"),
        )
        timing = TimingRule.InstantSpeed
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "64"
        artist = "Le Vuong"
        flavorText = "\"We're just in the process of improving upon what's already here.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b910851e-9332-4c83-a790-d379667cabfc.jpg?1764120376"
    }
}
