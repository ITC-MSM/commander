package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val KonstrariCharm = card("Konstrari Charm") {
    manaCost = "{R}{G}"
    colorIdentity = "RG"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Konstrari Charm deals 6 damage to target creature with flying.\n" +
        "• Put two +1/+1 counters on target creature. It gains trample until end of turn.\n" +
        "• Add {C}{C}{C}."

    spell {
        modal(chooseCount = 1) {
            mode("Konstrari Charm deals 6 damage to target creature with flying") {
                val t = target(
                    "target creature with flying",
                    TargetCreature(filter = TargetFilter.Creature.withKeyword(Keyword.FLYING))
                )
                effect = Effects.DealDamage(6, t)
            }
            mode("Put two +1/+1 counters on target creature. It gains trample until end of turn") {
                val t = target("target creature", Targets.Creature)
                effect = Effects.Composite(
                    Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, t),
                    Effects.GrantKeyword(Keyword.TRAMPLE, t)
                )
            }
            mode("Add {C}{C}{C}") {
                effect = Effects.AddColorlessMana(3)
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "138"
        artist = "Matheus Graef"
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d29dfa1-9582-47bc-8f42-62b611bdcc4e.jpg?1789127645"
        inBooster = false
    }
}
