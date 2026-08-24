package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Kuldotha Rebirth — Scars of Mirrodin #96
 * {R} · Sorcery
 *
 * As an additional cost to cast this spell, sacrifice an artifact.
 * Create three 1/1 red Goblin creature tokens.
 *
 * The sacrifice is a cast-time additional cost, not part of the resolution — it is paid when the
 * spell is announced, so countering it does not give the artifact back. The token needs no
 * `imageUri`: Scars of Mirrodin prints its own 1/1 red Goblin, so the set's token art sheet
 * resolves the art by identity.
 */
val KuldothaRebirth = card("Kuldotha Rebirth") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice an artifact.\n" +
        "Create three 1/1 red Goblin creature tokens."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Artifact))

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Goblin"),
            count = 3
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Goran Josic"
        flavorText = "All goblin rituals serve a dual purpose as fertility rites, even the destructive ones. Especially the destructive ones."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ee07266-a95d-4cd8-9863-1664922e9490.jpg?1783941723"
    }
}
