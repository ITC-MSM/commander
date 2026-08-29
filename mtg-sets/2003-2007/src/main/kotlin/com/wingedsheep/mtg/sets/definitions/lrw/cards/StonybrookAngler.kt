package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect

/**
 * Stonybrook Angler
 * {1}{U}
 * Creature — Merfolk Wizard
 * 1/2
 * {1}{U}, {T}: You may tap or untap target creature.
 *
 * A repeatable [Pestermite] on legs. The same idiom: the target is locked in when the ability goes
 * on the stack, the tap-or-untap choice is made on resolution, so a responding tap doesn't strand
 * you on the useless half.
 *
 * "You may" is not redundant with the modal choice — declining resolves the ability doing nothing,
 * which matters when the only legal target ends up somewhere you'd rather not touch.
 */
val StonybrookAngler = card("Stonybrook Angler") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 2
    oracleText = "{1}{U}, {T}: You may tap or untap target creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = MayEffect(
            ModalEffect(
                modes = listOf(
                    Mode.noTarget(TapUntapEffect(creature, tap = true), "Tap that creature"),
                    Mode.noTarget(TapUntapEffect(creature, tap = false), "Untap that creature")
                ),
                chooseCount = 1,
                countsAsModalSpell = false
            )
        )
        description = "{1}{U}, {T}: You may tap or untap target creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Larry MacDougall"
        flavorText = "\"Water is in the air, the trees, and the earth. Understand its motion, speak its language, and the subtle currents that flow through all living things will fall under your command.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/4/047709e7-c44f-47e4-a9cc-1d941264e454.jpg?1783942896"
    }
}
