package com.wingedsheep.mtg.sets.definitions.big.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Molten Duplication
 * {1}{R}
 * Sorcery
 * Create a token that's a copy of target artifact or creature you control, except it's an
 * artifact in addition to its other types. It gains haste until end of turn. Sacrifice it at
 * the beginning of the next end step.
 */
val MoltenDuplication = card("Molten Duplication") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create a token that's a copy of target artifact or creature you control, except it's an artifact in addition to its other types. It gains haste until end of turn. Sacrifice it at the beginning of the next end step."

    spell {
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.CreatureOrArtifact.youControl()))
        )
        // Haste is granted as a permanent keyword on the copy; because the token is sacrificed
        // at the next end step it never outlives "until end of turn" — same modeling Esika's
        // Chariot / Mardu Siegebreaker use for haste copies.
        effect = Effects.CreateTokenCopyOfTarget(
            target = t,
            addCardTypes = setOf("ARTIFACT"),
            addedKeywords = setOf(Keyword.HASTE),
            sacrificeAtStep = Step.END
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "14"
        artist = "Justyna Dura"
        flavorText = "No one had seen this side of Angeline before, not even her."
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdbe1ac1-461f-4746-a8d8-6c8dea2c97c6.jpg?1739804204"
    }
}
