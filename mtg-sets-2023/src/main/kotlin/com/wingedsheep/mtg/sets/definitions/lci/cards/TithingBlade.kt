package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tithing Blade // Consuming Sepulcher (CR 702.167, The Lost Caverns of Ixalan)
 * {1}{B}
 * Artifact // Artifact
 *
 * Front face — Tithing Blade ({1}{B}, Artifact)
 *   When this artifact enters, each opponent sacrifices a creature of their choice.
 *   Craft with creature {4}{B} ({4}{B}, Exile this artifact, Exile a creature you
 *   control or a creature card from your graveyard: Return this card transformed
 *   under its owner's control. Craft only as a sorcery.)
 *
 * Back face — Consuming Sepulcher (Artifact)
 *   At the beginning of your upkeep, each opponent loses 1 life and you gain 1 life.
 *
 * Implementation:
 *  - ETB edict: [Triggers.EntersBattlefield] + `Effects.Sacrifice` aimed at
 *    [Player.EachOpponent] — each opponent chooses their own creature to sacrifice
 *    (same shape as Susurian Dirgecraft / Cabal Executioner).
 *  - Craft: the `craft(...)` DSL helper wires the activated ability with an
 *    [com.wingedsheep.sdk.scripting.AbilityCost.Craft] material cost (exactly one
 *    creature: `minCount = maxCount = 1`, drawn from battlefield-controlled creatures
 *    and/or creature cards in your graveyard per CR 702.167b) plus the {4}{B} mana
 *    portion; resolution returns the card transformed via
 *    [com.wingedsheep.sdk.scripting.effects.ReturnSelfFromExileTransformedEffect].
 *  - Back-face drain: [Triggers.YourUpkeep] + composite of `Effects.LoseLife(1)` on
 *    [Player.EachOpponent] and `Effects.GainLife(1)` for the controller.
 */

private val TithingBladeFront = card("Tithing Blade") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, each opponent sacrifices a creature of their choice.\n" +
        "Craft with creature {4}{B} ({4}{B}, Exile this artifact, Exile a creature you control or a creature card from your graveyard: Return this card transformed under its owner's control. Craft only as a sorcery.)"

    // ETB: each opponent sacrifices a creature of their choice.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Sacrifice(
            GameObjectFilter.Creature,
            target = EffectTarget.PlayerRef(Player.EachOpponent)
        )
    }

    // Craft with creature {4}{B} — exactly one creature material.
    craft(
        filter = GameObjectFilter.Creature,
        cost = "{4}{B}",
        materialDescription = "creature",
        minCount = 1,
        maxCount = 1
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Michael Walsh"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbaa9a2d-e9fd-4746-a26c-f99ae731f024.jpg?1782694508"
    }
}

private val ConsumingSepulcher = card("Consuming Sepulcher") {
    manaCost = ""
    colorIdentity = "B"
    typeLine = "Artifact"
    oracleText = "At the beginning of your upkeep, each opponent loses 1 life and you gain 1 life."

    // At the beginning of your upkeep: drain each opponent for 1.
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.Composite(
            Effects.LoseLife(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Michael Walsh"
        flavorText = "\"Slake my thirst so that we both may be free.\"\n—Faded inscription"
        imageUri = "https://cards.scryfall.io/normal/back/d/b/dbaa9a2d-e9fd-4746-a26c-f99ae731f024.jpg?1782694508"
    }
}

val TithingBlade: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = TithingBladeFront,
    backFace = ConsumingSepulcher
)
