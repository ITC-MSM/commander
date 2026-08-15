package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Origin of Metalbending
 * {1}{G}
 * Instant — Lesson
 * Choose one —
 * • Destroy target artifact or enchantment.
 * • Put a +1/+1 counter on target creature you control. It gains indestructible
 *   until end of turn. (Damage and effects that say "destroy" don't destroy it.)
 */
val OriginOfMetalbending = card("Origin of Metalbending") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant — Lesson"
    oracleText = "Choose one —\n" +
        "• Destroy target artifact or enchantment.\n" +
        "• Put a +1/+1 counter on target creature you control. It gains indestructible " +
        "until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    spell {
        modal(chooseCount = 1) {
            mode("Destroy target artifact or enchantment.") {
                val t = target("target artifact or enchantment", Targets.ArtifactOrEnchantment)
                effect = Effects.Destroy(t)
            }
            mode(
                "Put a +1/+1 counter on target creature you control. It gains indestructible " +
                    "until end of turn."
            ) {
                val c = target("target creature you control", Targets.CreatureYouControl)
                effect = Effects.Composite(
                    Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, c),
                    Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, c),
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "187"
        artist = "Pauline Voss"
        flavorText = "Metal is just earth that has been purified and refined."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/25749f9a-260d-4bdc-bd28-429c12faa4a5.jpg?1764121273"
    }
}
