package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ChooseActionEffect
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.FeasibilityCheck
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bullseye, Death Dealer (MSH #209) — {2}{B/R} Legendary Creature — Human Assassin Villain, 2/3
 *
 * When Bullseye enters, you may sacrifice an artifact or discard a nonland card. When you do,
 * Bullseye deals 2 damage to any target.
 * {3}, {T}, Sacrifice an artifact or discard a nonland card: Bullseye deals 2 damage to any target.
 *
 * Implementation notes:
 * - The ETB is a genuine CR 603.12 reflexive trigger ("**When** you do"), so the damage target is
 *   chosen *after* the cost is paid, not when the ETB goes on the stack — a
 *   [ReflexiveTriggerEffect] whose `action` is the sacrifice-or-discard choice. That action is
 *   K'un-Lun Warrior's / Vision of Love's [ChooseActionEffect], one branch per printed option, each
 *   gated by the [FeasibilityCheck] that hides an option the controller can't perform. With neither
 *   option available `ReflexiveTriggerEffectExecutor.isActionFeasible` suppresses the "may" prompt
 *   entirely, so the reflexive can never fire without a cost actually being paid.
 * - The activated ability's cost is a *choice* between two independently payable non-mana costs.
 *   The engine has no choice/"or" cost for activated abilities (`AdditionalCost.Choice` is
 *   cast-time only), so this takes Bloodthorn Flail's faithful decomposition: two abilities,
 *   identical but for which half of the cost they pay. The player picks by choosing which one to
 *   activate, which is exactly the choice the printed single ability offers. Two differences, both
 *   harmless: the presentation (two menu entries instead of one with a cost prompt), and the
 *   announcement order — CR 602.2b routes ability activation through CR 601.2b–i, so on the printed
 *   card the target is announced (601.2c) *before* the cost is chosen and paid (601.2h), whereas
 *   picking one of these two abilities fixes the cost half first. That can't change a legal line:
 *   both halves are {T} abilities on the same permanent, so at most one is activatable per untap,
 *   and the choice is never visible to an opponent before it is made.
 */
val BullseyeDeathDealer = card("Bullseye, Death Dealer") {
    manaCost = "{2}{B/R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Human Assassin Villain"
    power = 2
    toughness = 3
    oracleText = "When Bullseye enters, you may sacrifice an artifact or discard a nonland card. " +
        "When you do, Bullseye deals 2 damage to any target.\n" +
        "{3}, {T}, Sacrifice an artifact or discard a nonland card: Bullseye deals 2 damage to " +
        "any target."

    // When Bullseye enters, you may sacrifice an artifact or discard a nonland card.
    // When you do, Bullseye deals 2 damage to any target.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = ReflexiveTriggerEffect(
            action = ChooseActionEffect(
                choices = listOf(
                    EffectChoice(
                        label = "Sacrifice an artifact",
                        effect = SacrificeEffect(filter = GameObjectFilter.Artifact),
                        feasibilityCheck = FeasibilityCheck.ControlsPermanentMatching(
                            filter = GameObjectFilter.Artifact
                        ),
                    ),
                    EffectChoice(
                        label = "Discard a nonland card",
                        effect = Patterns.Hand.discardCards(1, filter = GameObjectFilter.Nonland),
                        feasibilityCheck = FeasibilityCheck.HasCardsInZone(
                            zone = Zone.HAND,
                            filter = GameObjectFilter.Nonland,
                        ),
                    ),
                )
            ),
            optional = true,
            reflexiveEffect = Effects.DealDamage(2, EffectTarget.ContextTarget(0)),
            reflexiveTargetRequirements = listOf(Targets.Any),
            descriptionOverride = "You may sacrifice an artifact or discard a nonland card. " +
                "When you do, Bullseye deals 2 damage to any target.",
        )
        description = "When Bullseye enters, you may sacrifice an artifact or discard a nonland " +
            "card. When you do, Bullseye deals 2 damage to any target."
    }

    // {3}, {T}, Sacrifice an artifact: Bullseye deals 2 damage to any target.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}"),
            Costs.Tap,
            Costs.Sacrifice(GameObjectFilter.Artifact),
        )
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, victim)
        description = "{3}, {T}, Sacrifice an artifact: Bullseye deals 2 damage to any target."
    }

    // {3}, {T}, Discard a nonland card: Bullseye deals 2 damage to any target.
    // (The alternative half of "Sacrifice an artifact or discard a nonland card".)
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}"),
            Costs.Tap,
            Costs.Discard(GameObjectFilter.Nonland),
        )
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(2, victim)
        description = "{3}, {T}, Discard a nonland card: Bullseye deals 2 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Bartek Fedyczak"
        flavorText = "\"I don't miss.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd1f0b5f-5e0e-4da1-ab54-a62db5af3591.jpg?1783902903"
    }
}
