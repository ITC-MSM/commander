package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cabaretti Charm
 * {R}{G}{W}
 * Instant
 * Choose one —
 * • Cabaretti Charm deals damage equal to the number of creatures you control to target creature or planeswalker.
 * • Creatures you control get +1/+1 and gain trample until end of turn.
 * • Create two 1/1 green and white Citizen creature tokens.
 *
 * The three-mode charm shape: a dynamic burn mode counting the board
 * ([DynamicAmounts.creaturesYouControl]), the one-group-said-twice pump
 * ([Patterns.Group.pumpAndGrantToAll], the same call Overcome makes), and the set's shared
 * 1/1 green and white Citizen token.
 */
val CabarettiCharm = card("Cabaretti Charm") {
    manaCost = "{R}{G}{W}"
    colorIdentity = "GRW"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Cabaretti Charm deals damage equal to the number of creatures you control to target creature or planeswalker.\n• Creatures you control get +1/+1 and gain trample until end of turn.\n• Create two 1/1 green and white Citizen creature tokens."

    spell {
        modal(chooseCount = 1) {
            mode("Cabaretti Charm deals damage equal to the number of creatures you control to target creature or planeswalker") {
                val t = target("target", Targets.CreatureOrPlaneswalker)
                effect = Effects.DealDamage(DynamicAmounts.creaturesYouControl(), t)
            }
            mode("Creatures you control get +1/+1 and gain trample until end of turn") {
                effect = Patterns.Group.pumpAndGrantToAll(
                    power = 1,
                    toughness = 1,
                    keyword = Keyword.TRAMPLE,
                    filter = Filters.Group.creaturesYouControl,
                )
            }
            mode("Create two 1/1 green and white Citizen creature tokens") {
                effect = Effects.CreateToken(
                    power = 1,
                    toughness = 1,
                    colors = setOf(Color.GREEN, Color.WHITE),
                    creatureTypes = setOf("Citizen"),
                    count = 2,
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "Steve Argyle"
        imageUri = "https://cards.scryfall.io/normal/front/0/8/08f33c8a-8e93-4296-964b-da132a854b3b.jpg?1783923091"
    }
}
