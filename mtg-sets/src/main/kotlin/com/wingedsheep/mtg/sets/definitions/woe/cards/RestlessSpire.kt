package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Restless Spire — WOE #260
 * Land — Rare
 *
 * This land enters tapped.
 * {T}: Add {U} or {R}.
 * {U}{R}: Until end of turn, this land becomes a 2/1 blue and red Elemental creature with
 *   "During your turn, this creature has first strike." It's still a land.
 * Whenever this land attacks, scry 1.
 *
 * The Izzet member of the Wilds of Eldraine "Restless" creature-land cycle — see
 * [RestlessBivouac] and [RestlessFortress] for its siblings, and
 * [com.wingedsheep.mtg.sets.definitions.lci.cards.RestlessVents] for the Lost Caverns half. As
 * with the rest of the cycle the attack trigger is an *intrinsic* triggered ability of the land,
 * not one granted by the animate ability: in practice it's only live once {U}{R} has resolved
 * (a land has to be a creature to attack), but per the first Scryfall ruling it also fires if
 * something else animates the land.
 *
 * Unlike the rest of the cycle, the animated body arrives with a *quoted* ability rather than a
 * plain keyword, so the animate ability is a composite: [Effects.BecomeCreature] for the 2/1
 * Izzet Elemental body, plus a conditional first-strike grant for the quoted line. Within the
 * animation's own `Duration.EndOfTurn` window, "during your turn" cannot change answer — the
 * turn it's evaluated on is the turn the grant expires on — so the continuous conditional
 * collapses to a one-shot [Conditions.IsYourTurn] test at resolution: animate on your turn and
 * the body has first strike for that turn; animate on an opponent's turn (to ambush a blocker)
 * and it never does. The only divergence from the printed card is if an opponent gains control
 * of the land after it's been animated on your turn, which would flip "your turn" to false.
 */
val RestlessSpire = card("Restless Spire") {
    typeLine = "Land"
    colorIdentity = "UR"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {U} or {R}.\n" +
        "{U}{R}: Until end of turn, this land becomes a 2/1 blue and red Elemental creature with " +
        "\"During your turn, this creature has first strike.\" It's still a land.\n" +
        "Whenever this land attacks, scry 1."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Mana("{U}{R}")
        // The effect is a composite, whose auto-generated description would leak the internal
        // shape into the action-menu button. Say what the card says instead.
        description = "Until end of turn, this land becomes a 2/1 blue and red Elemental creature " +
            "with \"During your turn, this creature has first strike.\" It's still a land."
        effect = Effects.Composite(
            Effects.BecomeCreature(
                target = EffectTarget.Self,
                power = 2,
                toughness = 1,
                creatureTypes = setOf("Elemental"),
                colors = setOf(Color.BLUE.name, Color.RED.name),
                duration = Duration.EndOfTurn,
            ),
            ConditionalEffect(
                condition = Conditions.IsYourTurn,
                effect = Effects.GrantKeyword(
                    keyword = Keyword.FIRST_STRIKE,
                    target = EffectTarget.Self,
                    duration = Duration.EndOfTurn,
                ),
            ),
        )
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Library.scry(1)
        description = "Whenever this land attacks, scry 1."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "260"
        artist = "Sergey Glushakov"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66386fe8-9d3c-47f7-9cd3-4cd30051535f.jpg?1783915056"

        ruling(
            "2023-09-01",
            "If this becomes a creature because of an effect other than its own ability, its last " +
                "ability will still trigger whenever it attacks."
        )
        ruling(
            "2023-09-01",
            "If this becomes a creature but you haven't controlled it continuously since your most " +
                "recent turn began, you won't be able to activate its mana ability or attack with it " +
                "that turn."
        )
    }
}
