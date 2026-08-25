package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Demolish
 * {3}{R}
 * Sorcery
 * Destroy target artifact or land.
 *
 * One named target over [Targets.ArtifactOrLand] — the shared "artifact or land" union filter,
 * not a hand-rolled `Or` — consumed by [Effects.Destroy], which is the `byDestruction` move to the
 * graveyard so indestructible and regeneration still apply.
 */
val Demolish = card("Demolish") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or land."

    spell {
        val t = target("target", Targets.ArtifactOrLand)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "183"
        artist = "Gary Ruddell"
        flavorText = "\"Pound the steel until it fits. Doesn't work? Bash to bits.\"\n—Dwarven forging song"
        imageUri = "https://cards.scryfall.io/normal/front/9/1/9162a4df-ca6d-4f07-ae48-d333f1cb74b9.jpg?1783945234"
    }
}
