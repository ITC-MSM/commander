package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Jeering Instigator
 * {1}{R}
 * Creature — Goblin Rogue
 * 2/1
 * Morph {2}{R}
 * When this creature is turned face up, if it's your turn, gain control of another target creature
 * until end of turn. Untap that creature. It gains haste until end of turn.
 */
val JeeringInstigator = card("Jeering Instigator") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 2
    toughness = 1
    oracleText = "Morph {2}{R} (You may cast this card face down as a 2/2 creature for {3}. Turn it face up any time for its morph cost.)\nWhen Jeering Instigator is turned face up, if it's your turn, gain control of another target creature until end of turn. Untap that creature. It gains haste until end of turn."

    morph = "{2}{R}"

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        triggerCondition = Conditions.IsYourTurn
        val t = target("another creature", TargetCreature(filter = TargetFilter.OtherCreature))
        effect = Effects.Composite(
            Effects.GainControl(t, Duration.EndOfTurn),
            Effects.Untap(t),
            Effects.GrantKeyword(Keyword.HASTE, t)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "113"
        artist = "Willian Murai"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72777ccf-1cd8-45e8-8dfb-0a42550b5607.jpg?1562788522"
        ruling("2014-09-20", "Jeering Instigator's last ability can target any creature, even one that's untapped or one you already control.")
        ruling("2014-09-20", "Gaining control of a creature doesn't cause you to gain control of any Auras or Equipment attached to that creature.")
    }
}
