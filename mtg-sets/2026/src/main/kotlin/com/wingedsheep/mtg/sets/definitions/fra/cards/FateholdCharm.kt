package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.TargetSpellOrPermanent

val FateholdCharm = card("Fatehold Charm") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Draw a card. Empower Jace 2.\n" +
        "• Return target spell or creature to its owner's hand.\n" +
        "• Creatures you control get +1/+2 until end of turn."

    spell {
        modal(chooseCount = 1) {
            mode("Draw a card. Empower Jace 2") {
                effect = Effects.DrawCards(1).then(Patterns.Mechanic.empowerJace(2))
            }
            mode("Return target spell or creature to its owner's hand") {
                val t = target(
                    "target spell or creature",
                    TargetSpellOrPermanent(permanentFilter = GameObjectFilter.Creature)
                )
                effect = Effects.ReturnSpellOrPermanentToOwnersHand(t)
            }
            mode("Creatures you control get +1/+2 until end of turn") {
                effect = Patterns.Group.modifyStatsForAll(1, 2, GroupFilter(GameObjectFilter.Creature.youControl()))
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "132"
        artist = "Rovina Cai"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfc54011-647e-4428-bcdb-59400e1da49d.jpg?1789127637"
        inBooster = false
    }
}
