package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Song of Totentanz
 * {X}{R}
 * Sorcery
 *
 * Create X 1/1 black Rat creature tokens with "This token can't block."
 * Creatures you control gain haste until end of turn.
 *
 * The two halves are ordered, not simultaneous: the Rats are created first, so the haste grant —
 * a `ForEachInGroup` over creatures you control, snapshotted at resolution — catches them too.
 * Creatures that arrive later in the turn don't get haste (WOE ruling), which falls out of the
 * group being enumerated once, at resolution.
 */
val SongOfTotentanz = card("Song of Totentanz") {
    manaCost = "{X}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Create X 1/1 black Rat creature tokens with \"This token can't block.\" " +
        "Creatures you control gain haste until end of turn."

    spell {
        effect = Effects.Composite(
            woeRatToken(count = DynamicAmount.XValue),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.youControl()),
                Effects.GrantKeyword(Keyword.HASTE, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Randy Gallegos"
        flavorText = "Townsfolk tapped their feet and hummed along, unwittingly amplifying the tune " +
            "that would summon their doom."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/940e8bb7-0251-4fac-945c-d83618c10447.jpg?1783915088"

        ruling(
            "2023-09-01",
            "Only creatures you control at the time Song of Totentanz resolves, including the Rat tokens " +
                "it creates, will gain haste. Creatures you begin to control later in the turn won't be affected."
        )
    }
}
