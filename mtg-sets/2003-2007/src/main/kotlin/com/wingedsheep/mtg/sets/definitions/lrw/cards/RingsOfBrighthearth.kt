package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rings of Brighthearth
 * {3}
 * Artifact
 * Whenever you activate an ability, if it isn't a mana ability, you may pay {2}. If you do, copy
 * that ability. You may choose new targets for the copy.
 *
 * The whole card is three existing pieces stacked in the order the sentence reads:
 *
 * - **"if it isn't a mana ability" is part of the trigger, not an intervening "if".** It reads as a
 *   condition because Oracle templating puts it after the comma, but a mana ability never uses the
 *   stack (CR 605.1a) and so can never be the object of a "whenever you activate" trigger in the
 *   first place. [Triggers.YouActivateAbility] already carries that gate —
 *   `AbilityActivatedEvent(player = Player.You)` excludes mana abilities by default — so wiring the
 *   clause a second time as an `interveningIf` would be redundant, and worse, would re-check it on
 *   resolution where the printed clause never does.
 * - **The {2} is a resolution-time optional cost, not an additional cost of the trigger.**
 *   [OptionalCostEffect] (a `Gate.MayPay` over [PayManaCostEffect]) is the "you may pay … If you
 *   do, …" shape, and its one-yes-per-resolution nature is exactly the 2020-11-10 ruling: "You
 *   can't pay {2} more than once for each time the triggered ability of Rings of Brighthearth
 *   resolves."
 * - **The copy is [Effects.CopyTargetSpellOrAbility] over [EffectTarget.TriggeringEntity]** — the
 *   activated ability still sitting on the stack underneath this trigger. That executor already
 *   prompts for new targets per CR 707.10c, so the printed "You may choose new targets for the
 *   copy" needs no separate effect, and it copies the ability's chosen value of X along with its
 *   targets (the third ruling). Pit Automaton is the same pairing, reached through a delayed
 *   trigger instead of a printed one.
 *
 * The "activated an ability" event fires only *after* all costs are paid (CR 602.2), which is why
 * the sixth ruling holds for free: an ability whose cost sacrifices the Rings is activated at a
 * moment when the Rings is already gone, so the static index no longer carries this trigger.
 *
 * "An ability" is unrestricted by source — an ability of a permanent an opponent controls that
 * *you* activate (a granted "any player may activate" ability) still counts, as does a loyalty
 * ability, which is an activated ability.
 */
val RingsOfBrighthearth = card("Rings of Brighthearth") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "Whenever you activate an ability, if it isn't a mana ability, you may pay {2}. " +
        "If you do, copy that ability. You may choose new targets for the copy."

    triggeredAbility {
        trigger = Triggers.YouActivateAbility
        effect = OptionalCostEffect(
            cost = PayManaCostEffect(ManaCost.parse("{2}")),
            ifPaid = Effects.CopyTargetSpellOrAbility(EffectTarget.TriggeringEntity),
            descriptionOverride = "Pay {2}? If you do, copy that ability. You may choose new " +
                "targets for the copy."
        )
        description = "Whenever you activate an ability, if it isn't a mana ability, you may pay " +
            "{2}. If you do, copy that ability. You may choose new targets for the copy."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "259"
        artist = "Howard Lyon"
        flavorText = "\"Without flame, there would be no iron tools, no cooked meals, no purge of " +
            "old growth to make room for new.\"\n—Brighthearth creed"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbfd3898-cb06-4bb9-9d52-b319e1fa2217.jpg?1783942851"
        ruling(
            "2020-11-10",
            "An activated mana ability is one that produces mana as it resolves, not one that " +
                "costs mana to activate."
        )
        ruling(
            "2020-11-10",
            "You can't pay {2} more than once for each time the triggered ability of Rings of " +
                "Brighthearth resolves."
        )
        ruling("2020-11-10", "If the ability has {X} in its cost, the copy uses the same value of X.")
        ruling(
            "2020-11-10",
            "The triggered ability of Rings of Brighthearth and the copy it creates both resolve " +
                "before the ability that caused it to trigger. They resolve even if that ability " +
                "is countered."
        )
        ruling(
            "2020-11-10",
            "The copy will have the same targets as the ability it's copying unless you choose " +
                "new ones. You may change any number of the targets, including all of them or " +
                "none of them. If, for one of the targets, you can't choose a new legal target, " +
                "then it remains unchanged (even if the current target is illegal)."
        )
        ruling(
            "2020-11-10",
            "If paying the activation cost of the ability includes sacrificing Rings of " +
                "Brighthearth, the ability won't be copied. At the time the ability is considered " +
                "activated (after all costs are paid), Rings of Brighthearth is no longer on the " +
                "battlefield."
        )
    }
}
