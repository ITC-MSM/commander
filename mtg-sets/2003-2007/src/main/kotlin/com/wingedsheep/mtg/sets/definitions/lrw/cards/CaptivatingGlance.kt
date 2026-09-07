package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.GiveControlToTargetPlayerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Captivating Glance
 * {2}{U}
 * Enchantment — Aura
 * Enchant creature
 * At the beginning of your end step, clash with an opponent. If you win, gain control of enchanted
 * creature. Otherwise, that player gains control of enchanted creature.
 *
 * The card `Patterns.Mechanic.clash`'s `otherwise` leg was written for: both branches of the printed
 * "If you win … Otherwise …" are control changes, differing only in *who* ends up with the creature.
 *
 * The two halves are two existing effects, not one with a controller axis:
 *  - the win leg is [Effects.GainControl], which always gives the ability's controller control;
 *  - the lose leg is [GiveControlToTargetPlayerEffect], the sibling that takes an explicit
 *    [EffectTarget] for the new controller.
 *
 * "That player" is the opponent you clashed with, which the clash pattern has already recorded on
 * this Aura's `ChoiceSlot.OPPONENT` (its `ChooseOpponentForSourceEffect` prefix) — so the lose leg
 * reads it back as [Player.ChosenOpponent] rather than re-picking. That matters even though the two
 * are the same player in a two-player game: per the Scryfall ruling the opponent you clash with need
 * not control the enchanted creature, and per CR 701.30b the choice is made once for the clash.
 *
 * Both executors strip any previous Layer.CONTROL floating effect this Aura put on the same
 * permanent before adding their own, so the end-step trigger toggles control cleanly turn after turn
 * rather than stacking a new effect each time. The control change is `Duration.Permanent` and is not
 * tied to the Aura, matching the ruling that it "continues to apply even if Captivating Glance
 * leaves the battlefield".
 */
val CaptivatingGlance = card("Captivating Glance") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "At the beginning of your end step, clash with an opponent. If you win, gain control of " +
        "enchanted creature. Otherwise, that player gains control of enchanted creature. (Each " +
        "clashing player reveals the top card of their library, then puts that card on their " +
        "choice of the top or bottom. A player wins if their card had a greater mana value.)"

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Patterns.Mechanic.clash(
            ifYouWin = Effects.GainControl(EffectTarget.EnchantedCreature),
            otherwise = GiveControlToTargetPlayerEffect(
                permanent = EffectTarget.EnchantedCreature,
                newController = EffectTarget.PlayerRef(Player.ChosenOpponent)
            )
        )
        description = "clash with an opponent. If you win, gain control of enchanted creature. " +
            "Otherwise, that player gains control of enchanted creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "55"
        artist = "Dan Dos Santos"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/642be353-a46a-4d14-a35a-7716fd552870.jpg?1783942904"
        ruling(
            "2007-10-01",
            "The ability triggers at the end of Captivating Glance's controller's turn. \"You\" " +
                "refers to the controller of Captivating Glance, not the controller of the " +
                "enchanted creature."
        )
        ruling(
            "2007-10-01",
            "The opponent you clash with doesn't have to be the controller of the enchanted creature."
        )
        ruling(
            "2007-10-01",
            "If you win the clash, you gain control of the enchanted creature. If you don't win the " +
                "clash, the other player gains control of the enchanted creature (even if that " +
                "player didn't win the clash either)."
        )
        ruling("2007-10-01", "Captivating Glance's controller never changes as a result of this card.")
        ruling(
            "2007-10-01",
            "Captivating Glance may cause a player to gain control of a creature they already " +
                "control. This layers a new control effect on that creature, but doing so rarely " +
                "has any visible effect."
        )
        ruling(
            "2007-10-01",
            "Captivating Glance's control-change effect continues to apply even if Captivating " +
                "Glance leaves the battlefield."
        )
    }
}
