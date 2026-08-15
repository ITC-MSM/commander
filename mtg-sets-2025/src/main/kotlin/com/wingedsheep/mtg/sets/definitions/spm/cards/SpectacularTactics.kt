package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Spectacular Tactics
 * {1}{W}
 * Instant
 * Choose one —
 * • Put a +1/+1 counter on target creature you control. It gains hexproof until end of turn.
 * • Destroy target creature with power 4 or greater.
 */
val SpectacularTactics = card("Spectacular Tactics") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose one —\n• Put a +1/+1 counter on target creature you control. It gains hexproof until end of turn.\n• Destroy target creature with power 4 or greater."
    spell {
        modal(chooseCount = 1) {
            mode("Put a +1/+1 counter on target creature you control. It gains hexproof until end of turn") {
                val t = target("target", TargetCreature(filter = TargetFilter.Creature.youControl()))
                effect = Effects.Composite(
                    AddCountersEffect(counterType = Counters.PLUS_ONE_PLUS_ONE, count = 1, target = t),
                    Effects.GrantKeyword(Keyword.HEXPROOF, t)
                )
            }
            mode("Destroy target creature with power 4 or greater") {
                val t = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(4)))
                effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
            }
        }
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "15"
        artist = "Zoltan Boros"
        flavorText = "\"Not bad, kid, but you've got a lot to learn.\"\n—Spider-Woman, Jessica Drew"
        imageUri = "https://cards.scryfall.io/normal/front/8/3/836b4246-f1f2-4495-8664-650dda70ed4f.jpg?1757376833"
    }
}
