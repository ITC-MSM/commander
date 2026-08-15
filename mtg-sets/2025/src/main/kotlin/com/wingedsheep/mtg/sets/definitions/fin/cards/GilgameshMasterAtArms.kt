package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Gilgamesh, Master-at-Arms
 * {4}{R}{R}
 * Legendary Creature — Human Samurai
 * 6/6
 * Whenever Gilgamesh enters or attacks, look at the top six cards of your library. You may put
 *   any number of Equipment cards from among them onto the battlefield. Put the rest on the
 *   bottom of your library in a random order. When you put one or more Equipment onto the
 *   battlefield this way, you may attach one of them to a Samurai you control.
 *
 * "Enters or attacks" is the Frodo, Determined Hero shape: two sibling triggered abilities (the
 * engine has no combined enters-or-attacks trigger) sharing one [lookAtTopSixPutEquipment] body.
 *
 * The body is a Gather → Select(any number, filtered to Equipment) → Move(battlefield) /
 * Move(bottom, random) pipeline — the [com.wingedsheep.sdk.dsl.LibraryPatterns.lookAtTopAndKeep]
 * shape, but with a filtered any-number selection (only Equipment cards are eligible — the rest
 * of the six aren't offered) and a battlefield destination instead of hand/graveyard, matching
 * the Beatrix, Loyal General / Colossus of the Blood Age inline-pipeline idiom for "any number"
 * resolution-time choices that aren't `target`ed.
 *
 * "When you put one or more Equipment onto the battlefield this way, you may attach one of them
 * to a Samurai you control" has neither half ("one of them" / "a Samurai you control") marked
 * `target` in the oracle text, so both are resolution-time choices, not cast/trigger-time
 * targets. It's gated by an `ifNotEmpty` branch on the battlefield-bound collection so it
 * only runs when at least one Equipment was actually put onto the battlefield, then composes two
 * `chooseUpTo(1)` picks (which equipment / which Samurai — `chooseUpTo` already covers "you may"
 * by allowing zero) feeding [Effects.AttachTargetEquipmentToCreature], which no-ops gracefully if
 * either resolves to nothing (no Samurai you control, or the player declines).
 *
 * Per Scryfall ruling (2025-06-06) this "When you put..." sentence is technically a *reflexive*
 * triggered ability that goes on the stack as its own object — relevant because Job select
 * Equipment (e.g. Samurai's Katana) has its own "when this Equipment enters" trigger, and the two
 * would fire simultaneously, with the controller choosing stack order. The engine has no general
 * primitive for spawning a brand-new stack object mid-resolution that interleaves with ordinary
 * simultaneous triggers (`CreateDelayedTriggerEffect` is built for future-step/future-event
 * delayed triggers, not an immediate same-batch reflexive trigger; building that generically is
 * `add-feature` scope, not a single-card change). This card models the reflexive sentence as an
 * inline pipeline continuation instead, mirroring the same simplification already accepted for
 * Weapons Vendor's "you may pay {1}. When you do, attach target Equipment..." in this set. Per
 * the same ruling, the final board state is identical regardless of which order the two abilities
 * would have been stacked in — Job select always re-attaches the Equipment to its own Hero token
 * afterward — so the only rules nuance lost is a narrow instant-speed response window between the
 * two would-be stack objects, not any observable outcome.
 */
val GilgameshMasterAtArms = card("Gilgamesh, Master-at-Arms") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Samurai"
    power = 6
    toughness = 6
    oracleText = "Whenever Gilgamesh enters or attacks, look at the top six cards of your library. " +
        "You may put any number of Equipment cards from among them onto the battlefield. Put the " +
        "rest on the bottom of your library in a random order. When you put one or more Equipment " +
        "onto the battlefield this way, you may attach one of them to a Samurai you control."

    // "Whenever Gilgamesh enters …"
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = lookAtTopSixPutEquipment()
    }

    // "… or attacks"
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = lookAtTopSixPutEquipment()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "139"
        artist = "Lorenzo Mastroianni"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1eb81329-fb7a-4347-b96c-9960a5c48e87.jpg?1782686495"

        ruling(
            "2025-06-06",
            "You can't use the reflexive triggered ability to try to attach an Equipment to a " +
                "creature if that Equipment can't legally be attached to that creature."
        )
        ruling(
            "2025-06-06",
            "If you put an Equipment with job select onto the battlefield with Gilgamesh's " +
                "ability, the job select ability and the reflexive triggered ability will both " +
                "trigger, and you'll choose the order in which those abilities go on the stack. " +
                "Regardless of the order chosen, the job select ability will create a Hero token " +
                "and attach the Equipment to the Hero token after both abilities have resolved."
        )
    }
}

private fun lookAtTopSixPutEquipment(): Effect = Effects.Pipeline {
    val looked = gather(CardSource.TopOfLibrary(DynamicAmount.Fixed(6)), name = "looked")
    val (equipment, rest) = chooseAnyNumberSplit(
        from = looked,
        filter = GameObjectFilter.Artifact.withSubtype(Subtype.EQUIPMENT),
        prompt = "Choose any number of Equipment cards to put onto the battlefield",
        selectedLabel = "Put onto the battlefield",
        remainderLabel = "Put on the bottom of your library",
        showAllCards = true
    )
    val onBattlefield = moveTracked(
        equipment,
        CardDestination.ToZone(Zone.BATTLEFIELD),
        name = "putOntoBattlefield"
    )
    move(
        rest,
        CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
        order = CardOrder.Random
    )
    // "When you put one or more Equipment onto the battlefield this way, you may attach one of
    // them to a Samurai you control."
    ifNotEmpty(onBattlefield) {
        val chosenEquipment = chooseUpTo(
            1,
            from = onBattlefield,
            useTargetingUI = true,
            prompt = "You may choose one of the Equipment put onto the battlefield this way to attach"
        )
        val samurai = gather(
            GameObjectFilter.Creature.withSubtype(Subtype.SAMURAI),
            player = Player.You,
            name = "samurai"
        )
        val chosenSamurai = chooseUpTo(
            1,
            from = samurai,
            useTargetingUI = true,
            prompt = "You may choose a Samurai you control to attach it to"
        )
        run(
            Effects.AttachTargetEquipmentToCreature(
                equipmentTarget = EffectTarget.PipelineTarget(chosenEquipment.key, 0),
                creatureTarget = EffectTarget.PipelineTarget(chosenSamurai.key, 0)
            )
        )
    }
}
