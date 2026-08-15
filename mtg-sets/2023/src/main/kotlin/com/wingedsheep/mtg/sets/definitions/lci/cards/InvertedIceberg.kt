package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.MayEffect

/**
 * Inverted Iceberg // Iceberg Titan (CR 702.167, The Lost Caverns of Ixalan #60)
 * {1}{U}
 * Artifact // Artifact Creature — Golem
 *
 * Front face — Inverted Iceberg ({1}{U}, Artifact)
 *   When this artifact enters, mill a card, then draw a card.
 *   Craft with artifact {4}{U}{U} ({4}{U}{U}, Exile this artifact, Exile another
 *   artifact you control or an artifact card from your graveyard: Return this card
 *   transformed under its owner's control. Craft only as a sorcery.)
 *
 * Back face — Iceberg Titan (Artifact Creature — Golem, 6/6)
 *   Whenever this creature attacks, you may tap or untap target artifact or creature.
 *
 * Implementation:
 *  - Front ETB: [Effects.Composite] sequencing `Patterns.Library.mill(1)` then
 *    `Effects.DrawCards(1)` — mill fully resolves before the draw, matching the
 *    "mill a card, then draw a card" ordering.
 *  - Craft: the `craft(...)` helper wires [com.wingedsheep.sdk.scripting.AbilityCost.Craft]
 *    (exactly one artifact material: `minCount = 1, maxCount = 1`) plus the {4}{U}{U} mana
 *    cost at sorcery speed; resolution returns the source from exile transformed via
 *    [com.wingedsheep.sdk.scripting.effects.ReturnSelfFromExileTransformedEffect].
 *  - Back attack trigger: declared target ([Targets.CreatureOrArtifact], chosen when the
 *    trigger goes on the stack) with a [MayEffect]-wrapped two-mode [ModalEffect]
 *    (tap / untap via [TapUntapEffect]) decided at resolution — the same idiom as
 *    Sewer-veillance Cam / Gandalf the Grey's "you may tap or untap target ..." clause.
 */

private val InvertedIcebergFront = card("Inverted Iceberg") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, mill a card, then draw a card. (To mill a card, put the top card of your library into your graveyard.)\n" +
        "Craft with artifact {4}{U}{U} ({4}{U}{U}, Exile this artifact, Exile another artifact you control or an artifact card from your graveyard: Return this card transformed under its owner's control. Craft only as a sorcery.)"

    // When this artifact enters, mill a card, then draw a card.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Library.mill(1),
            Effects.DrawCards(1)
        )
    }

    // Craft with artifact {4}{U}{U} — exactly one artifact material.
    craft(
        filter = GameObjectFilter.Artifact,
        cost = "{4}{U}{U}",
        materialDescription = "artifact",
        minCount = 1,
        maxCount = 1
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Campbell White"
        imageUri = "https://cards.scryfall.io/normal/front/a/c/ac5e9a53-cc4f-4ced-8088-5a73d619eae3.jpg?1782694562"
    }
}

private val IcebergTitan = card("Iceberg Titan") {
    manaCost = ""
    colorIdentity = "U"
    typeLine = "Artifact Creature — Golem"
    power = 6
    toughness = 6
    oracleText = "Whenever this creature attacks, you may tap or untap target artifact or creature."

    // Whenever this creature attacks, you may tap or untap target artifact or creature.
    // MayEffect + Effects.ChooseAction is the proven tap-or-untap idiom (Gandalf the Grey);
    // the engine asks the may-question and locks the target when the trigger goes on the
    // stack, then the tap/untap choice is made at resolution.
    triggeredAbility {
        trigger = Triggers.Attacks
        val permanent = target("target artifact or creature", Targets.CreatureOrArtifact)
        effect = MayEffect(
            Effects.ChooseAction(
                listOf(
                    EffectChoice("Tap it", Effects.Tap(permanent)),
                    EffectChoice("Untap it", Effects.Untap(permanent))
                )
            ),
            descriptionOverride = "You may tap or untap target artifact or creature"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Campbell White"
        flavorText = "The force of a blizzard. The rage of a monstrosaur."
        imageUri = "https://cards.scryfall.io/normal/back/a/c/ac5e9a53-cc4f-4ced-8088-5a73d619eae3.jpg?1782694562"
    }
}

val InvertedIceberg: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = InvertedIcebergFront,
    backFace = IcebergTitan
)
