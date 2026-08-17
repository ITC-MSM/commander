package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.EffectChoice
import com.wingedsheep.sdk.scripting.effects.FeasibilityCheck
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rakdos, Patron of Chaos — Murders at Karlov Manor #224
 * {4}{B}{R} · Legendary Creature — Demon · 6/6
 *
 * Flying, trample
 * At the beginning of your end step, target opponent may sacrifice two nonland, nontoken permanents
 * of their choice. If they don't, you draw two cards.
 *
 * A six-mana 6/6 flier that taxes the table every turn it survives: two real permanents or two cards,
 * chosen by the victim, every end step. In multiplayer the target is re-chosen each turn, so the tax
 * rotates.
 *
 * **The "may" is an opponent-side choice between two named actions**, which is exactly
 * [Effects.ChooseAction] with `player` set to the targeted opponent — the same shape as Terrapact
 * Intimidator's wording-identical "target opponent may … If they don't, …". Both halves are effects
 * the choosing player selects between, not a cost they pay, and that distinction is load-bearing here:
 *
 * - A `MayEffect(decisionMaker = opponent, otherwise = draw)` would prompt the right player, but the
 *   engine evaluates a gate's feasibility against the *ability's controller*, not the decision maker.
 *   Rakdos's controller having two spare permanents would then wrongly enable the option.
 * - Worse, `ForceSacrifice` auto-sacrifices when a player has at most `count` legal permanents, so an
 *   opponent who accepted while holding a single permanent would lose that one — sacrificing *one*
 *   where the card demands two. `ChooseAction` avoids both: its
 *   [FeasibilityCheck.ControlsPermanentMatching] is tested against the chooser, so an opponent who
 *   can't find two is never offered the option, the remaining choice auto-executes, and the draw
 *   happens without a prompt. That is the rules-correct outcome — they can't sacrifice two, so they
 *   don't, and "if they don't" fires.
 *
 * "Of their choice" is the default for `Effects.Sacrifice(…, target = opponent)`: the named player is
 * both the sacrificer and the chooser, prompted for exactly two permanents. `NonlandPermanent
 * .nontoken()` is the printed filter — tokens are excluded so a Clue-and-Treasure board can't buy its
 * way out cheaply.
 *
 * The draw is `EffectTarget.Controller`, not the trigger's target: "you" is Rakdos's controller.
 * Nothing in either branch refers back to Rakdos, so both still happen if it leaves the battlefield
 * after the trigger goes on the stack.
 */
val RakdosPatronOfChaos = card("Rakdos, Patron of Chaos") {
    manaCost = "{4}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Legendary Creature — Demon"
    power = 6
    toughness = 6
    oracleText = "Flying, trample\n" +
        "At the beginning of your end step, target opponent may sacrifice two nonland, nontoken " +
        "permanents of their choice. If they don't, you draw two cards."

    keywords(Keyword.FLYING, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        val opponent = target("target opponent", Targets.Opponent)
        val fodder = GameObjectFilter.NonlandPermanent.nontoken()
        effect = Effects.ChooseAction(
            choices = listOf(
                EffectChoice(
                    label = "Sacrifice two nonland, nontoken permanents",
                    effect = Effects.Sacrifice(fodder, count = 2, target = opponent),
                    feasibilityCheck = FeasibilityCheck.ControlsPermanentMatching(fodder, count = 2)
                ),
                EffectChoice(
                    label = "Let Rakdos's controller draw two cards",
                    effect = Effects.DrawCards(2, EffectTarget.Controller)
                )
            ),
            player = opponent
        )
        description = "At the beginning of your end step, target opponent may sacrifice two " +
            "nonland, nontoken permanents of their choice. If they don't, you draw two cards."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "224"
        artist = "Joshua Raphael"
        flavorText = "\"Finally, a performance worthy of my applause!\""
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc6fd2d5-8eb2-4265-a1bf-d4ae635285af.jpg?1783912841"
    }
}
