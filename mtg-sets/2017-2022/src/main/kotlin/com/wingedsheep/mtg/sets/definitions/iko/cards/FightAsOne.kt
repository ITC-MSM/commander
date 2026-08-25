package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Fight as One
 * {W}
 * Instant
 * Choose one or both —
 * • Target Human creature you control gets +1/+1 and gains indestructible until end of turn.
 * • Target non-Human creature you control gets +1/+1 and gains indestructible until end of turn.
 *
 * "Choose one or both" is a two-mode modal with `chooseCount = 2, minChooseCount = 1`. Each mode
 * carries its own target requirement, so only the picked modes demand a target — and the two
 * filters partition your creatures by the Human subtype, which is what makes casting both a
 * two-creature trick rather than a single doubled pump.
 */
val FightAsOne = card("Fight as One") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Choose one or both —\n" +
        "• Target Human creature you control gets +1/+1 and gains indestructible until end of turn.\n" +
        "• Target non-Human creature you control gets +1/+1 and gains indestructible until end of turn."

    spell {
        modal(chooseCount = 2, minChooseCount = 1) {
            mode("Target Human creature you control gets +1/+1 and gains indestructible until end of turn.") {
                val t = target(
                    "target",
                    TargetCreature(
                        filter = TargetFilter(
                            GameObjectFilter.Creature.withSubtype(Subtype.HUMAN).youControl()
                        )
                    )
                )
                effect = Effects.Composite(
                    Effects.ModifyStats(1, 1, t),
                    Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
                )
            }
            mode("Target non-Human creature you control gets +1/+1 and gains indestructible until end of turn.") {
                val t = target(
                    "target",
                    TargetCreature(
                        filter = TargetFilter(
                            GameObjectFilter.Creature.notSubtype(Subtype.HUMAN).youControl()
                        )
                    )
                )
                effect = Effects.Composite(
                    Effects.ModifyStats(1, 1, t),
                    Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
                )
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "12"
        artist = "Bryan Sola"
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c62ea13-9bd5-46ce-a861-68cf9a2c6f8c.jpg"
    }
}
