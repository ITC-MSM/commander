package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dauntless Dourbark
 * {3}{G}
 * Creature — Treefolk Warrior
 * Printed power and toughness are both star (a characteristic-defining ability).
 * Dauntless Dourbark's power and toughness are each equal to the number of Forests you control
 * plus the number of Treefolk you control.
 * This creature has trample as long as you control another Treefolk.
 *
 * The CDA is a literal [DynamicAmount.Add] of two independent battlefield counts, which is what
 * the printed "plus" asks for and what the 2007-10-01 rulings require: the Dourbark counts itself
 * (it is a Treefolk you control), and a permanent that is *both* a Forest and a Treefolk is
 * counted **twice** — once by each half. Folding the two into one `withAnySubtype` filter would
 * silently count such a permanent once and get the card wrong.
 *
 * "Forests" is a land type, so that half counts lands with the Forest subtype; the bare noun
 * "Treefolk" counts every Treefolk *permanent* you control, creature or not.
 *
 * Trample is a [ConditionalStaticAbility] over `excludeSelf = true` — "another Treefolk" doesn't
 * see the Dourbark itself, so it stays correct even if the Dourbark stops being a Treefolk.
 */
val DauntlessDourbark = card("Dauntless Dourbark") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Warrior"
    power = 0
    toughness = 0
    oracleText = "Dauntless Dourbark's power and toughness are each equal to the number of " +
        "Forests you control plus the number of Treefolk you control.\n" +
        "This creature has trample as long as you control another Treefolk."

    dynamicStats(
        DynamicAmount.Add(
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Land.withSubtype(Subtype.FOREST)
            ).count(),
            DynamicAmounts.battlefield(
                Player.You,
                GameObjectFilter.Permanent.withSubtype(Subtype.TREEFOLK)
            ).count()
        )
    )

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source()),
            condition = Conditions.YouControl(
                GameObjectFilter.Permanent.withSubtype(Subtype.TREEFOLK),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "203"
        artist = "Jeremy Jarvis"
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad87cb77-6b49-4e93-9151-29f53b48dd4b.jpg?1783942866"
        ruling("2007-10-01", "Dauntless Dourbark counts itself when determining its power and toughness.")
        ruling("2007-10-01", "A permanent that's both a Forest and a Treefolk will be counted " +
            "twice when determining Dauntless Dourbark's power and toughness.")
    }
}
