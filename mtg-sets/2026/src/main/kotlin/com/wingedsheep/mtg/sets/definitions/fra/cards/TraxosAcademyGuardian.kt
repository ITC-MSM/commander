package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostGating
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

val TraxosAcademyGuardian = card("Traxos, Academy Guardian") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Artifact Creature — Dragon Construct"
    power = 1
    toughness = 5
    oracleText = "This spell costs {2} less to cast if you've cast a noncreature spell this turn.\nFlying, vigilance\nProwess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)"

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.SelfCast,
            modification = CostModification.ReduceGeneric(2),
            gating = CostGating.OnlyIf(Conditions.YouCastSpellsThisTurn(atLeast = 1, filter = GameObjectFilter.Noncreature)),
        )
    }

    keywords(Keyword.FLYING, Keyword.VIGILANCE)
    prowess()

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "222"
        artist = "Aaron Miller"
        flavorText = "\"Tolaria has stood for a thousand years. You're looking at why.\"\n—Lyra, Tolarian headmaster"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a349800f-b634-4e74-a9d9-185df37ad909.jpg?1789568451"
        inBooster = false
    }
}
