package com.wingedsheep.mtg.sets.definitions.all.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Pillage — Alliances #76
 * {1}{R}{R} · Sorcery
 *
 * Destroy target artifact or land. It can't be regenerated.
 *
 * `noRegenerate` composes the "can't be regenerated" marker *before* the destroy, so the marker
 * is on the permanent by the time it is moved to the graveyard.
 */
val Pillage = card("Pillage") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or land. It can't be regenerated."

    spell {
        val t = target(
            "target",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact or GameObjectFilter.Land))
        )
        effect = Effects.Destroy(t, noRegenerate = true)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "76"
        artist = "Richard Kane Ferguson"
        flavorText = "\"Were they to reduce us to ash, we would clog their throats and sting their eyes in payment.\"\n—Lovisa Coldeyes, Balduvian Chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/3/8/389ecb50-b007-4086-89fb-ec2daa5afdcf.jpg?1783947176"
    }
}
