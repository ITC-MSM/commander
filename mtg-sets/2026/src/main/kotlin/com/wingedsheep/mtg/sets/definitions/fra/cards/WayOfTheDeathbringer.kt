package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.grantedLoyaltyAbility
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.effects.FeasibilityCheck
import com.wingedsheep.sdk.scripting.effects.IfYouDoEffect
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

val WayOfTheDeathbringer = card("Way of the Deathbringer") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Enchantment"
    oracleText = "When Way of the Deathbringer enters, empower Jace 5. (Put five loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")\n" +
        "Planeswalkers you control have \"[−2]: You may sacrifice a creature. If you do, create a 4/4 green Beast creature token with trample.\""

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Mechanic.empowerJace(5)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = grantedLoyaltyAbility(-2) {
                effect = MayEffect(
                    effect = IfYouDoEffect(
                        action = Effects.Pipeline {
                            val creatures = gather(GameObjectFilter.Creature, player = Player.You)
                            val chosen = chooseExactly(
                                1,
                                from = creatures,
                                useTargetingUI = true,
                                prompt = "Choose a creature to sacrifice",
                            )
                            sacrifice(chosen)
                        },
                        ifYouDo = Effects.CreateToken(
                            power = 4,
                            toughness = 4,
                            colors = setOf(Color.GREEN),
                            creatureTypes = setOf("Beast"),
                            keywords = setOf(Keyword.TRAMPLE),
                            imageUri = "https://cards.scryfall.io/normal/front/8/5/859bda9a-fa90-4ad3-b0c1-6fc62e27c12f.jpg?1789736256"
                        ),
                    ),
                    descriptionOverride = "You may sacrifice a creature. If you do, create a 4/4 green Beast creature token with trample.",
                    feasibility = FeasibilityCheck.ControlsPermanentMatching(GameObjectFilter.Creature.youControl()),
                )
                description = "You may sacrifice a creature. If you do, create a 4/4 green Beast creature token with trample."
            },
            filter = GroupFilter(GameObjectFilter.Planeswalker.youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12dd46b2-e892-4660-b120-55766fd4d878.jpg?1789729576"
        inBooster = false
    }
}
