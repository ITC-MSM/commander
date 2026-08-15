package com.wingedsheep.mtg.sets.definitions.ody.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Zombify
 * {3}{B}
 * Sorcery
 *
 * Return target creature card from your graveyard to the battlefield.
 */
val Zombify = card("Zombify") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Return target creature card from your graveyard to the battlefield."

    spell {
        val creature = target("target creature card from your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = Effects.PutOntoBattlefield(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "171"
        artist = "Mark Romanoski"
        flavorText = "\"The first birth celebrates life. The second birth mocks it.\"\n—Mystic elder"
        imageUri = "https://cards.scryfall.io/normal/front/5/1/513a2a6f-9ae6-42cb-b75f-6b45fc35f36e.jpg?1562909871"
    }
}
