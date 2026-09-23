package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreatureOrPlaneswalker

/**
 * The granted −4 declares "target player" before "up to one target creature or planeswalker",
 * the reverse of the Oracle sentence. Cast-time target slots are positional, so an optional
 * target has to come last to be declinable. CR 601.2c fixes no order between separate targets,
 * and the effects bind by name, so the reversal changes nothing else.
 */
val WayOfTheWarlord = card("Way of the Warlord") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Warlord enters, empower Jace 5. (Put five loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Planeswalkers you control have \"[−4]: This planeswalker deals 2 damage to up to one target creature or planeswalker and 2 damage to target player.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(5)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(-4) {
                val player = target("target player", Targets.Player)
                val permanent = target("up to one target creature or planeswalker", TargetCreatureOrPlaneswalker(optional = true))
                effect = Effects.Composite(
                    Effects.DealDamage(2, permanent),
                    Effects.DealDamage(2, player)
                )
                description = "This planeswalker deals 2 damage to up to one target creature or planeswalker and 2 damage to target player."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "255"
        artist = "Danny Schwartz"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d86e410-20c4-4248-96bf-5780ece6274a.jpg?1789729585"
        inBooster = false
    }
}
