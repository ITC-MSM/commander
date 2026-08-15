package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetOther

/**
 * Saheeli, the Sun's Brilliance
 * {U}{R}
 * Legendary Creature — Human Artificer
 * 2/2
 * {U}{R}, {T}: Create a token that's a copy of another target creature or artifact you
 * control, except it's an artifact in addition to its other types. It gains haste.
 * Sacrifice it at the beginning of the next end step.
 */
val SaheeliTheSunsBrilliance = card("Saheeli, the Sun's Brilliance") {
    manaCost = "{U}{R}"
    colorIdentity = "UR"
    typeLine = "Legendary Creature — Human Artificer"
    power = 2
    toughness = 2
    oracleText = "{U}{R}, {T}: Create a token that's a copy of another target creature or " +
        "artifact you control, except it's an artifact in addition to its other types. It " +
        "gains haste. Sacrifice it at the beginning of the next end step."

    // "another target creature or artifact you control" → TargetOther excludes the source
    // itself from a controller-scoped creature/artifact target. The copy keeps its other
    // types but gains the artifact type, and haste is added as a permanent keyword; since
    // the token is sacrificed at the next end step it never outlives the haste (same
    // modeling Molten Duplication / The Jolly Balloon Man use). No sorcery-speed clause.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}{R}"), Costs.Tap)
        val t = target(
            "another target creature or artifact you control",
            TargetOther(
                baseRequirement = TargetObject(
                    filter = TargetFilter(GameObjectFilter.CreatureOrArtifact.youControl())
                )
            )
        )
        effect = Effects.CreateTokenCopyOfTarget(
            target = t,
            addCardTypes = setOf("ARTIFACT"),
            addedKeywords = setOf(Keyword.HASTE),
            sacrificeAtStep = Step.END,
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "239"
        artist = "Cynthia Sheppard"
        flavorText = "What began as a single gift for Huatli became a battalion of automatons " +
            "unlike anything ever seen on Ixalan."
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0ba99b60-c7d0-4041-a065-f2c510745223.jpg?1782694420"
    }
}
