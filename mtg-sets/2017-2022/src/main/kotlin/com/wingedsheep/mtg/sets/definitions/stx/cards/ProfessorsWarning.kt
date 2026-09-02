package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Professor's Warning — Strixhaven: School of Mages #84 (canonical printing)
 * {B} · Instant
 *
 * Choose one —
 * • Put a +1/+1 counter on target creature.
 * • Target creature gains indestructible until end of turn. (Damage and effects that say "destroy" don't destroy it.)
 *
 * A choose-one modal (the Valorous Stance shape) where each mode carries its own creature target:
 * the first is [Effects.AddCounters] of a single +1/+1 counter, the second [Effects.GrantKeyword]
 * of indestructible with the default until-end-of-turn duration.
 */
val ProfessorsWarning = card("Professor's Warning") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText =
        "Choose one —\n" +
        "• Put a +1/+1 counter on target creature.\n" +
        "• Target creature gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    spell {
        modal(chooseCount = 1) {
            mode("Put a +1/+1 counter on target creature.") {
                val creature = target("target", Targets.Creature)
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
            }
            mode("Target creature gains indestructible until end of turn.") {
                val creature = target("target", Targets.Creature)
                effect = Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, creature)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "84"
        artist = "Kieran Yanner"
        flavorText = "Professor Onyx urged the twins to prepare for the dark mage Extus, sounding very much like someone familiar with the rise of tyrants."
        imageUri = "https://cards.scryfall.io/normal/front/8/6/862fded6-e474-4b20-86d8-fd9af5f25b1a.jpg?1783927363"
    }
}
