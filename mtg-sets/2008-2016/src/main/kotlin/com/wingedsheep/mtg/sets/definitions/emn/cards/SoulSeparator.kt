package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Soul Separator
 * {3}
 * Artifact
 * {5}, {T}, Sacrifice this artifact: Exile target creature card from your graveyard. Create a token
 * that's a copy of that card, except it's 1/1, it's a Spirit in addition to its other types, and it
 * has flying. Create a black Zombie creature token with power equal to that card's power and
 * toughness equal to that card's toughness.
 *
 * Modeled after Sauron, the Necromancer: exile the targeted graveyard creature card, then reference
 * "that card" (its [CardComponent] survives the move to exile) via [EffectTarget.ContextTarget].
 * - The copy token uses `overridePower`/`overrideToughness = 1` for the 1/1, `addedSubtypes` for
 *   "Spirit in addition to its other types", and `addedKeywords` for flying.
 * - The Zombie token's power/toughness are read from the exiled card's printed base stats via
 *   [DynamicAmount.EntityProperty] (a card outside the battlefield shows its printed characteristics).
 */
val SoulSeparator = card("Soul Separator") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{5}, {T}, Sacrifice this artifact: Exile target creature card from your graveyard. " +
        "Create a token that's a copy of that card, except it's 1/1, it's a Spirit in addition to " +
        "its other types, and it has flying. Create a black Zombie creature token with power equal " +
        "to that card's power and toughness equal to that card's toughness."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{5}"), Costs.Tap, Costs.SacrificeSelf)
        val graveyardCreature = target(
            "target creature card from your graveyard",
            Targets.CreatureCardInYourGraveyard
        )
        effect = Effects.Composite(listOf(
            Effects.Exile(graveyardCreature),
            Effects.CreateTokenCopyOfTarget(
                target = EffectTarget.ContextTarget(0),
                overridePower = 1,
                overrideToughness = 1,
                addedSubtypes = setOf(Subtype("Spirit")),
                addedKeywords = setOf(Keyword.FLYING)
            ),
            Effects.CreateDynamicToken(
                dynamicPower = DynamicAmount.EntityProperty(
                    EntityReference.Target(0), EntityNumericProperty.Power
                ),
                dynamicToughness = DynamicAmount.EntityProperty(
                    EntityReference.Target(0), EntityNumericProperty.Toughness
                ),
                colors = setOf(Color.BLACK),
                creatureTypes = setOf("Zombie")
            )
        ))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "199"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb9b1158-dfc4-4fe9-b970-338be8a99662.jpg?1782711810"
    }
}
