package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Clay-Fired Bricks // Cosmium Kiln (CR 702.167, The Lost Caverns of Ixalan)
 * {1}{W}
 * Artifact // Artifact
 *
 * Front face — Clay-Fired Bricks ({1}{W}, Artifact)
 *   When this artifact enters, search your library for a basic Plains card, reveal it,
 *   put it into your hand, then shuffle. You gain 2 life.
 *   Craft with artifact {5}{W}{W} ({5}{W}{W}, Exile this artifact, Exile another artifact
 *   you control or an artifact card from your graveyard: Return this card transformed
 *   under its owner's control. Craft only as a sorcery.)
 *
 * Back face — Cosmium Kiln (Artifact)
 *   When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens.
 *   Creatures you control get +1/+1.
 *
 * Implementation: the front face's ETB is [Effects.Composite] sequencing the atomic
 * [Patterns.Library.searchLibrary] pipeline (basic Plains → reveal → hand → shuffle) before
 * [Effects.GainLife]. The `craft(...)` helper wires the activated ability with an
 * [com.wingedsheep.sdk.scripting.AbilityCost.Craft] material cost (exactly one artifact:
 * minCount = maxCount = 1) paired with the printed mana cost; resolution returns the card
 * from exile as the back face via
 * [com.wingedsheep.sdk.scripting.effects.ReturnSelfFromExileTransformedEffect]. The back
 * face's ETB fires off the normal battlefield-entry event when the craft return puts it
 * onto the battlefield transformed, creating the Gnomes with
 * [com.wingedsheep.sdk.scripting.effects.CreateTokenEffect]; its anthem is a [ModifyStats]
 * static over [GameObjectFilter.Creature.youControl] (Glorious Anthem pattern).
 */

private val ClayFiredBricksFront = card("Clay-Fired Bricks") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, search your library for a basic Plains card, " +
        "reveal it, put it into your hand, then shuffle. You gain 2 life.\n" +
        "Craft with artifact {5}{W}{W} ({5}{W}{W}, Exile this artifact, Exile another artifact " +
        "you control or an artifact card from your graveyard: Return this card transformed " +
        "under its owner's control. Craft only as a sorcery.)"

    // ETB: search your library for a basic Plains card, reveal it, put it into your hand,
    // then shuffle. You gain 2 life.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.BasicLand.withSubtype(Subtype.PLAINS),
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
                shuffleAfter = true
            ),
            Effects.GainLife(2)
        )
        description = "When this artifact enters, search your library for a basic Plains card, " +
            "reveal it, put it into your hand, then shuffle. You gain 2 life."
    }

    // Craft with artifact {5}{W}{W} — exactly one artifact material.
    craft(
        filter = GameObjectFilter.Artifact,
        cost = "{5}{W}{W}",
        materialDescription = "artifact",
        minCount = 1,
        maxCount = 1
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Steve Ellis"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8aece300-656b-4e0d-a45f-aa7feaff0a4e.jpg?1782694608"
    }
}

private val CosmiumKiln = card("Cosmium Kiln") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens.\n" +
        "Creatures you control get +1/+1."

    // ETB: create two 1/1 colorless Gnome artifact creature tokens.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            creatureTypes = setOf("Gnome"),
            count = 2,
            artifactToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/6/d/6def709a-53b3-4520-9544-74ab6472d256.jpg?1782731572"
        )
        description = "When this artifact enters, create two 1/1 colorless Gnome artifact creature tokens."
    }

    // Creatures you control get +1/+1.
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Steve Ellis"
        flavorText = "\"I love the smell of freshly baked gnomes!\"\n—Yelha, Oltec artisan"
        imageUri = "https://cards.scryfall.io/normal/back/8/a/8aece300-656b-4e0d-a45f-aa7feaff0a4e.jpg?1782694608"
    }
}

val ClayFiredBricks: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = ClayFiredBricksFront,
    backFace = CosmiumKiln
)
