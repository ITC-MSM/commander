package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Item Shopkeep
 * {1}{R}
 * Creature — Human Citizen
 * 2/2
 *
 * Whenever you attack, target attacking equipped creature gains menace until end of turn.
 * (It can't be blocked except by two or more creatures.)
 */
val ItemShopkeep = card("Item Shopkeep") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Citizen"
    power = 2
    toughness = 2
    oracleText = "Whenever you attack, target attacking equipped creature gains menace until end of turn. " +
        "(It can't be blocked except by two or more creatures.)"

    triggeredAbility {
        trigger = Triggers.YouAttack
        val attacker = target(
            "attacking equipped creature",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.attacking().equipped()))
        )
        effect = Effects.GrantKeyword(Keyword.MENACE, attacker)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "142"
        artist = "IWAO"
        flavorText = "\"What can I do for you?\""
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bd2db3f5-fd0d-4817-af90-6bea1f07e16b.jpg?1748706293"
    }
}
