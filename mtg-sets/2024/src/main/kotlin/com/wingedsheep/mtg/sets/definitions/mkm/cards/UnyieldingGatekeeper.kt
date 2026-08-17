package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Unyielding Gatekeeper — Murders at Karlov Manor #35
 * {1}{W} · Creature — Elephant Cleric · 3/2 · Rare
 *
 * Disguise {1}{W}
 * When this creature is turned face up, exile another target nonland permanent. If you controlled
 * it, return it to the battlefield tapped. Otherwise, its controller creates a 2/2 white and blue
 * Detective creature token.
 *
 * Two cards in one trigger: a blink for your own enters-the-battlefield permanent, or unconditional
 * exile removal for an opponent's — bought at the price of handing them a 2/2. Both halves are
 * bought at instant speed and can't be responded to, because turning a permanent face up is a
 * special action (CR 701.34a); the only window an opponent gets is with the trigger already on the
 * stack and its target locked in.
 *
 * **The branch is decided before the exile, not after.** "If you controlled it" is read as the
 * ability resolves, and both printed rulings turn on that: controlling the permanent earlier in the
 * turn doesn't count, and one you *do* control returns under **your** control regardless of who
 * owns it. [ConditionalEffect] evaluates its condition up front as a synchronous state test, so
 * [Conditions.TargetMatchesFilter] still sees the permanent on the battlefield with its controller
 * intact. Each branch then does its own exile — hoisting the exile out of the conditional and
 * testing afterwards would be testing a controller that no longer exists.
 *
 * The return is [RestorationAngel]'s two-move blink with [ZonePlacement.Tapped], plus an explicit
 * `controllerOverride = EffectTarget.Controller`: the default return is under the card's *owner's*
 * control, which is the wrong player for the one case the second ruling calls out — a permanent you
 * control but don't own. A card that changes zones is a new object, so nothing survives the trip;
 * that is correct here, the printed card wants a fresh permanent.
 *
 * The consolation token uses [EffectTarget.TargetController], which reads last-known information
 * after the exile (the Bovine Intervention shape), so the opponent creates their own Detective per
 * CR 111.1. It takes its art from MKM's `tokenArt` layer, so no `imageUri` is baked in — the same
 * shape [ChalkOutline] and [InsideSource] use for theirs.
 */
val UnyieldingGatekeeper = card("Unyielding Gatekeeper") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Cleric"
    oracleText = "Disguise {1}{W} (You may cast this card face down for {3} as a 2/2 creature " +
        "with ward {2}. Turn it face up any time for its disguise cost.)\n" +
        "When this creature is turned face up, exile another target nonland permanent. If you " +
        "controlled it, return it to the battlefield tapped. Otherwise, its controller creates a " +
        "2/2 white and blue Detective creature token."
    power = 3
    toughness = 2

    disguise = "{1}{W}"

    triggeredAbility {
        trigger = Triggers.TurnedFaceUp
        val permanent = target("another target nonland permanent", Targets.OtherNonlandPermanent)
        effect = ConditionalEffect(
            condition = Conditions.TargetMatchesFilter(
                GameObjectFilter.NonlandPermanent.youControl()
            ),
            effect = Effects.Move(permanent, Zone.EXILE)
                .then(
                    Effects.Move(
                        permanent,
                        Zone.BATTLEFIELD,
                        placement = ZonePlacement.Tapped,
                        controllerOverride = EffectTarget.Controller,
                    )
                ),
            elseEffect = Effects.Composite(
                Effects.Exile(permanent),
                Effects.CreateToken(
                    power = 2,
                    toughness = 2,
                    colors = setOf(Color.WHITE, Color.BLUE),
                    creatureTypes = setOf("Detective"),
                    controller = EffectTarget.TargetController,
                ),
            ),
        )
        description = "When this creature is turned face up, exile another target nonland " +
            "permanent. If you controlled it, return it to the battlefield tapped. Otherwise, " +
            "its controller creates a 2/2 white and blue Detective creature token."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "35"
        artist = "Borja Pindado"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3a0d597-d2df-4aaf-8084-c8eeda64ce60.jpg?1783912917"

        ruling(
            "2024-02-02",
            "If you controlled the target permanent earlier in the turn but not at the time " +
                "Unyielding Gatekeeper's last ability resolves, you won't return it to the " +
                "battlefield tapped. The player who controlled that permanent at the time it was " +
                "exiled will create a Detective."
        )
        ruling(
            "2024-02-02",
            "If the controller of Unyielding Gatekeeper's last ability controlled the target " +
                "permanent at the time the ability began to resolve, the permanent will return to " +
                "the battlefield tapped under that player's control, regardless of who its owner is."
        )
        ruling(
            "2024-02-02",
            "Because the permanent is on the battlefield both before and after it's turned face " +
                "up, turning a permanent face up doesn't cause any enters-the-battlefield " +
                "abilities to trigger."
        )
        ruling(
            "2024-02-02",
            "Any time you have priority, you may turn the face-down creature face up by revealing " +
                "what its disguise cost is and paying that cost. This is a special action. It " +
                "doesn't use the stack and can't be responded to."
        )
    }
}
