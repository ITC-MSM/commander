package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Dreamshackle Geist
 * {1}{U}{U}
 * Creature — Spirit
 * 3/1
 *
 * Flying
 * At the beginning of combat on your turn, choose up to one —
 * • Tap target creature.
 * • Target creature doesn't untap during its controller's next untap step.
 *
 * The Sawblade Slinger idiom for "choose up to one": one [ModalEffect] with `chooseCount = 1,
 * minChooseCount = 0`, so declining both modes is legal and the ability leaves the stack having
 * done nothing (CR 700.2b).
 *
 * Because it is a *triggered* modal ability, the mode and its target are locked in as the ability
 * is put onto the stack (CR 603.3c) — the controller does not get to re-choose on resolution, and
 * the ability is removed from the stack if its one target has become illegal by then.
 *
 * `countsAsModalSpell` is deliberately left at its default. The flag exists for a `ModalEffect` used
 * as an implementation shortcut for a mechanic that is *not* printed as a modal (gift), and it gates
 * `SpellCastEvent.chosenModesCount` — which a triggered ability never produces. The printed wording
 * here is a genuine modal, so the default is both the faithful value and the one Assay reads back.
 *
 * Mode 2 is the Elvish Hunter rail: an ability flag with
 * [Duration.UntilAfterAffectedControllersNextUntap], keyed to the *affected* creature's controller,
 * so the effect survives until that player's next untap step rather than the Geist controller's.
 */
val DreamshackleGeist = card("Dreamshackle Geist") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    power = 3
    toughness = 1
    oracleText = "Flying\n" +
        "At the beginning of combat on your turn, choose up to one —\n" +
        "• Tap target creature.\n" +
        "• Target creature doesn't untap during its controller's next untap step."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.BeginCombat
        effect = ModalEffect(
            modes = listOf(
                Mode.withTarget(
                    Effects.Tap(EffectTarget.ContextTarget(0)),
                    TargetCreature(),
                    "Tap target creature"
                ),
                Mode.withTarget(
                    Effects.GrantKeyword(
                        AbilityFlag.DOESNT_UNTAP,
                        EffectTarget.ContextTarget(0),
                        Duration.UntilAfterAffectedControllersNextUntap,
                    ),
                    TargetCreature(),
                    "Target creature doesn't untap during its controller's next untap step"
                )
            ),
            chooseCount = 1,
            minChooseCount = 0,
        )
        description = "At the beginning of combat on your turn, choose up to one — Tap target " +
            "creature, or target creature doesn't untap during its controller's next untap step."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "58"
        artist = "Andreas Zafiratos"
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1b81d90b-708a-48c9-a478-e3b0a3d7e982.jpg?1783924894"
    }
}
