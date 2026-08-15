package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.craft
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Oteclan Landmark // Oteclan Levitator (CR 702.167, The Lost Caverns of Ixalan)
 * {W}
 * Artifact // Artifact Creature — Golem
 *
 * Front face — Oteclan Landmark ({W}, Artifact)
 *   When this artifact enters, scry 2.
 *   Craft with artifact {2}{W} ({2}{W}, Exile this artifact, Exile another artifact you
 *   control or an artifact card from your graveyard: Return this card transformed under
 *   its owner's control. Craft only as a sorcery.)
 *
 * Back face — Oteclan Levitator (Artifact Creature — Golem, 1/4)
 *   Flying
 *   Whenever this creature attacks, target attacking creature without flying gains
 *   flying until end of turn.
 *
 * Implementation:
 *  - Front ETB: [Triggers.EntersBattlefield] → [Patterns.Library.scry] (2).
 *  - Craft: the `craft(...)` helper wires [com.wingedsheep.sdk.scripting.AbilityCost.Craft]
 *    (material filter [GameObjectFilter.Artifact], exactly one material — minCount = maxCount = 1
 *    per "Craft with artifact") paired with the {2}{W} mana cost, resolving via
 *    [com.wingedsheep.sdk.scripting.effects.ReturnSelfFromExileTransformedEffect] at
 *    sorcery speed.
 *  - Back attack trigger: [Triggers.Attacks] targeting
 *    [TargetFilter.AttackingCreature].withoutKeyword(FLYING) →
 *    [Effects.GrantKeyword] (FLYING, until end of turn).
 */

private val OteclanLandmarkFront = card("Oteclan Landmark") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Artifact"
    oracleText = "When this artifact enters, scry 2.\n" +
        "Craft with artifact {2}{W} ({2}{W}, Exile this artifact, Exile another artifact you control or an artifact card from your graveyard: Return this card transformed under its owner's control. Craft only as a sorcery.)"

    // When this artifact enters, scry 2.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.scry(2)
    }

    // "Craft with artifact" = exactly one artifact material.
    craft(
        filter = GameObjectFilter.Artifact,
        cost = "{2}{W}",
        materialDescription = "artifact",
        minCount = 1,
        maxCount = 1
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Warren Mahy"
        imageUri = "https://cards.scryfall.io/normal/front/0/9/099a1d1c-72ba-468e-9385-8f93e1fce001.jpg?1782694588"
    }
}

private val OteclanLevitator = card("Oteclan Levitator") {
    manaCost = ""
    colorIdentity = "W"
    typeLine = "Artifact Creature — Golem"
    power = 1
    toughness = 4
    oracleText = "Flying\n" +
        "Whenever this creature attacks, target attacking creature without flying gains flying until end of turn."

    keywords(Keyword.FLYING)

    // Whenever this creature attacks, target attacking creature without flying
    // gains flying until end of turn.
    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target(
            "attacking creature without flying",
            TargetCreature(filter = TargetFilter.AttackingCreature.withoutKeyword(Keyword.FLYING))
        )
        effect = Effects.GrantKeyword(Keyword.FLYING, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Warren Mahy"
        flavorText = "Dusk Legion invaders sought lone Oltec travelers to prey upon. The Core's guidestones did not approve."
        imageUri = "https://cards.scryfall.io/normal/back/0/9/099a1d1c-72ba-468e-9385-8f93e1fce001.jpg?1782694588"
    }
}

val OteclanLandmark: CardDefinition = CardDefinition.doubleFacedPermanent(
    frontFace = OteclanLandmarkFront,
    backFace = OteclanLevitator
)
