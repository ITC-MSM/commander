package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Buccaneer's Bravado
 * {1}{R}
 * Instant
 *
 * Choose one —
 * • Target creature gets +1/+1 and gains first strike until end of turn.
 * • Target Pirate gets +1/+1 and gains double strike until end of turn.
 *
 * The second mode's "target Pirate" is the bare tribal noun, so it reaches a noncreature Pirate
 * permanent too — [GameObjectFilter.Permanent], not `.Creature`.
 */
val BuccaneersBravado = card("Buccaneer's Bravado") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Target creature gets +1/+1 and gains first strike until end of turn.\n" +
        "• Target Pirate gets +1/+1 and gains double strike until end of turn."

    spell {
        modal {
            mode("Target creature gets +1/+1 and gains first strike until end of turn") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.ModifyStats(1, 1, creature) then
                    Effects.GrantKeyword(Keyword.FIRST_STRIKE, creature)
            }
            mode("Target Pirate gets +1/+1 and gains double strike until end of turn") {
                val pirate = target(
                    "target Pirate",
                    TargetPermanent(
                        filter = TargetFilter(
                            GameObjectFilter.Permanent.withSubtype(Subtype.PIRATE)
                        )
                    )
                )
                effect = Effects.ModifyStats(1, 1, pirate) then
                    Effects.GrantKeyword(Keyword.DOUBLE_STRIKE, pirate)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Bram Sels"
        flavorText = "Never underestimate the power of panache."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d82c583f-3d2b-4c7f-a6f4-97da150423b4.jpg?1783935302"
    }
}
