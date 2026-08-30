package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantAttackUnless
import com.wingedsheep.sdk.scripting.CantBlockUnless
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Blind-Spot Giant
 * {2}{R}
 * Creature — Giant Warrior
 * 4/3
 * This creature can't attack or block unless you control another Giant.
 *
 * "Can't attack or block" is two separate restrictions in the SDK — one checked at attacker
 * declaration, one at blocker declaration — so it takes two static abilities over the same
 * condition. `excludeSelf` is what makes it "*another* Giant".
 */
val BlindSpotGiant = card("Blind-Spot Giant") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant Warrior"
    power = 4
    toughness = 3
    oracleText = "This creature can't attack or block unless you control another Giant."

    val anotherGiant = Conditions.YouControl(
        GameObjectFilter.Permanent.withSubtype(Subtype.GIANT),
        excludeSelf = true
    )

    staticAbility {
        ability = CantAttackUnless(anotherGiant)
    }
    staticAbility {
        ability = CantBlockUnless(anotherGiant)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Jim Murray"
        flavorText = "Among the solitude-loving giantkind, teamwork is unusual. But he appreciates hearing the occasional \"Swing down and to your left.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75aaca31-8ed5-4e8a-8332-1cca77903f88.jpg?1783942880"
    }
}
