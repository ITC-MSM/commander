package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Valiant Knight
 * {3}{W}
 * Creature — Human Knight
 * 3/4
 * Other Knights you control get +1/+1.
 * {3}{W}{W}: Knights you control gain double strike until end of turn.
 *
 * Two separate abilities, not one: the lord is a static [ModifyStats] with `excludeSelf = true`
 * ("Other"), while the pump is an activated group grant that *does* include this creature.
 * Both read a bare tribal noun — "Knights", not "Knight creatures" — so the filter is
 * `Permanent.withSubtype`, not `Creature.withSubtype`.
 */
val ValiantKnight = card("Valiant Knight") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 4
    oracleText = "Other Knights you control get +1/+1.\n" +
        "{3}{W}{W}: Knights you control gain double strike until end of turn."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.KNIGHT).youControl(),
                excludeSelf = true
            )
        )
    }

    activatedAbility {
        cost = Costs.Mana("{3}{W}{W}")
        effect = Patterns.Group.grantKeywordToAll(
            keyword = Keyword.DOUBLE_STRIKE,
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.KNIGHT).youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "Jakub Kasper"
        flavorText = "\"Defeat is no reason for retreat. It is a sign we must redouble our efforts to win this fight.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6ad750bc-850b-483b-8910-eb6562f925bc.jpg"
    }
}
