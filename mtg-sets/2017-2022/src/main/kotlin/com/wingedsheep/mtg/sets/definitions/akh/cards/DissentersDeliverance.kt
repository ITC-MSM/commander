package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Dissenter's Deliverance
 * {1}{G}
 * Instant
 * Destroy target artifact.
 * Cycling {G} ({G}, Discard this card: Draw a card.)
 */
val DissentersDeliverance = card("Dissenter's Deliverance") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy target artifact.\n" +
            "Cycling {G} ({G}, Discard this card: Draw a card.)"

    spell {
        val t = target("target", Targets.Artifact)
        effect = Effects.Destroy(t)
    }

    keywordAbility(KeywordAbility.cycling("{G}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Bastien L. Deharme"
        flavorText = "\"When all doubts have melted away, the worthy will meet the Hour of Eternity and earn a place at the God-Pharaoh's side.\"\n—*The Accounting of Hours*"
        imageUri = "https://cards.scryfall.io/normal/front/5/4/543eb854-bac7-468f-b3b0-d9987cfac318.jpg?1783936477"
    }
}
