package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Shredded Sails
 * {1}{R}
 * Instant
 * Choose one —
 * • Destroy target artifact.
 * • Shredded Sails deals 4 damage to target creature with flying.
 * Cycling {2} ({2}, Discard this card: Draw a card.)
 */
val ShreddedSails = card("Shredded Sails") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Destroy target artifact.\n• Shredded Sails deals 4 damage to target creature with flying.\nCycling {2} ({2}, Discard this card: Draw a card.)"

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target artifact") {
                val t = target("target artifact", Targets.Artifact)
                effect = Effects.Destroy(t)
            }
            mode("Shredded Sails deals 4 damage to target creature with flying") {
                val t = target("target creature with flying", Targets.CreatureWithKeyword(Keyword.FLYING))
                effect = Effects.DealDamage(4, t)
            }
        }
    }

    keywordAbility(KeywordAbility.cycling("{2}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Titus Lunter"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8b10219a-aa72-4431-9a6a-984109a605c8.jpg?1783931044"
    }
}
