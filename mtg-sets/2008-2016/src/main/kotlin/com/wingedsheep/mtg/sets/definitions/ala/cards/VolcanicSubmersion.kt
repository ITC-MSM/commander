package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Volcanic Submersion
 * {4}{R}
 * Sorcery
 * Destroy target artifact or land.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 *
 * The spell half is one named target over [Targets.ArtifactOrLand] — the shared "artifact or land"
 * union filter, not a hand-rolled `Or` — consumed by [Effects.Destroy], which is the
 * `byDestruction` move to the graveyard so indestructible and regeneration still apply. Cycling is
 * declared as [KeywordAbility.cycling], which carries its own cost and lowers to the discard-and-draw
 * activated ability; no `keywords(...)` entry is needed alongside it.
 */
val VolcanicSubmersion = card("Volcanic Submersion") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or land.\n" +
        "Cycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", Targets.ArtifactOrLand)
        effect = Effects.Destroy(t)
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "121"
        artist = "Trevor Claxton"
        flavorText = "A dragon's death is almost as feared as its life. Old, dying dragons throw themselves into volcanoes, causing massive upheaval and widespread disaster."
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0ec1f1fa-41c9-4bc0-9171-902cd456aa73.jpg"
    }
}
