package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.PayLifeEffect
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Meathook Massacre II
 * {X}{X}{B}{B}{B}{B}
 * Legendary Enchantment
 *
 * When Meathook Massacre II enters, each player sacrifices X creatures of their choice.
 * Whenever a creature you control dies, you may pay 3 life. If you do, return that card
 * under your control with a finality counter on it.
 * Whenever a creature an opponent controls dies, they may pay 3 life. If they don't,
 * return that card under your control with a finality counter on it.
 *
 * The three abilities all key off the dying creature (`EffectTarget.TriggeringEntity`) and reanimate
 * it under *your* control (the ability's controller) with a finality counter — the established
 * "return … with a finality counter" composition (Rite of the Moth, Zoraline): `Move(GRAVEYARD →
 * BATTLEFIELD)` + `AddCounters(FINALITY)`.
 *
 *  - **ETB** — [DynamicAmount.CastX] reads the `{X}{X}` value off the cast enchantment and rides it
 *    onto the permanent, so `Effects.Sacrifice(Creature, count = CastX, target = Each)` makes each
 *    player choose X of their creatures to sacrifice (APNAP order, CR 101.4).
 *
 *  - **Your creature dies** — opt-in [OptionalCostEffect] (Gate.MayPay): *you* may pay 3 life, and
 *    only then is the card returned. Cost and decision default to the ability's controller (you).
 *
 *  - **An opponent's creature dies** — pay-to-prevent, the inverse reading, via [PayOrSufferEffect]
 *    with `player = Player.TriggeringPlayer`: the *dying creature's last-known controller* (the
 *    opponent) decides and pays. If they decline, the suffer effect — returning the card under your
 *    control — resolves as part of *your* ability, so it runs under the ability's controller (you
 *    steal it). Routing the pay/suffer decision and the life payment to a non-controller, and running
 *    the consequence under the ability's controller, is the engine work this card needed (DSK engine
 *    gap #16): `Player.TriggeringPlayer` now resolves to a dying creature's last-known controller
 *    (TriggerContext populates `triggeringPlayerId` from `ZoneChangeEvent.lastKnownController`), and
 *    the PayOrSuffer consequence runs under the ability controller, not the payer.
 *
 * Per the printed rulings, tokens (no card to return) and creatures that have already left the
 * graveyard simply don't come back — both fall out naturally because `EffectTarget.TriggeringEntity`
 * finds nothing to move.
 */
val MeathookMassacreII = card("Meathook Massacre II") {
    manaCost = "{X}{X}{B}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Enchantment"
    oracleText = "When Meathook Massacre II enters, each player sacrifices X creatures of their choice.\n" +
        "Whenever a creature you control dies, you may pay 3 life. If you do, return that card under " +
        "your control with a finality counter on it.\n" +
        "Whenever a creature an opponent controls dies, they may pay 3 life. If they don't, return " +
        "that card under your control with a finality counter on it."

    // When Meathook Massacre II enters, each player sacrifices X creatures of their choice.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Sacrifice(
            filter = GameObjectFilter.Creature,
            count = DynamicAmount.CastX,
            target = EffectTarget.PlayerRef(Player.Each)
        )
    }

    // Whenever a creature you control dies, you may pay 3 life. If you do, return that card under
    // your control with a finality counter on it.
    triggeredAbility {
        trigger = Triggers.YourCreatureDies
        effect = OptionalCostEffect(
            cost = PayLifeEffect(3),
            ifPaid = returnDeadCreatureUnderYourControl(),
            descriptionOverride = "You may pay 3 life. If you do, return that card under your control " +
                "with a finality counter on it."
        )
    }

    // Whenever a creature an opponent controls dies, they may pay 3 life. If they don't, return that
    // card under your control with a finality counter on it.
    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.opponentControls(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY
        )
        effect = PayOrSufferEffect(
            // The dying creature's last-known controller (the opponent) decides and pays the 3 life.
            player = EffectTarget.PlayerRef(Player.TriggeringPlayer),
            cost = Costs.pay.PayLife(3),
            // If they don't pay, you (the ability's controller) steal the card.
            suffer = returnDeadCreatureUnderYourControl()
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "108"
        artist = "Tiffany Turrill"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3db59d06-a226-42b2-8f01-6b63a6eea83f.jpg?1726286251"

        ruling(
            "2024-09-20",
            "If a creature that dies leaves the graveyard before Meathook Massacre II's second or " +
                "third ability (as appropriate) resolves, that creature won't return to the " +
                "battlefield regardless of whether or not life was paid."
        )
        ruling(
            "2024-09-20",
            "When a token creature dies, Meathook Massacre II's second or third ability (as " +
                "appropriate) will trigger. The appropriate player has the option to pay 3 life, but " +
                "regardless of their choice, the token won't return to the battlefield."
        )
        ruling(
            "2024-09-20",
            "Finality counters work on any permanent, not only creatures. If a permanent with a " +
                "finality counter on it would be put into a graveyard from the battlefield, exile it instead."
        )
        ruling(
            "2024-09-20",
            "Multiple finality counters on a single permanent are redundant."
        )
    }
}

/**
 * "Return that card under your control with a finality counter on it." Shared by Meathook Massacre
 * II's second and third abilities: move the dying creature ([EffectTarget.TriggeringEntity]) from
 * the graveyard onto the battlefield under the ability's controller, then add a finality counter
 * (CR 122.6). The move is a no-op for tokens or for a card that has
 * already left the graveyard, exactly as the printed rulings require.
 */
private fun returnDeadCreatureUnderYourControl() = Effects.Composite(
    Effects.Move(
        target = EffectTarget.TriggeringEntity,
        destination = Zone.BATTLEFIELD,
        fromZone = Zone.GRAVEYARD,
        controllerOverride = EffectTarget.Controller
    ),
    AddCountersEffect(
        counterType = Counters.FINALITY,
        count = 1,
        target = EffectTarget.TriggeringEntity
    )
)
