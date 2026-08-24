package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Oxidda Scrapmelter — Scars of Mirrodin #101
 * {3}{R} · Creature — Beast · 3 / 3
 *
 * When this creature enters, destroy target artifact.
 *
 * A plain SELF-bound [Triggers.EntersBattlefield] over [Effects.Destroy]. The trigger is not
 * optional and its target is not "up to", so it must pick an artifact if one is on the battlefield
 * — including one of yours when the opponent has none. `Destroy` lowers to a graveyard move flagged
 * `byDestruction`, which is what lets indestructible and regeneration see it.
 */
val OxiddaScrapmelter = card("Oxidda Scrapmelter") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 3
    toughness = 3
    oracleText = "When this creature enters, destroy target artifact."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val artifact = target("target artifact", Targets.Artifact)
        effect = Effects.Destroy(artifact)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "101"
        artist = "Igor Kieryluk"
        flavorText = "It subsists on a diet rich in screaming metal and molten blood."
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c64fe85b-e471-489a-8c38-2357da1c7969.jpg?1783941722"
    }
}
