package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ReflexiveTriggerEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cemetery Desecrator
 * {4}{B}{B}
 * Creature — Zombie
 * 4/4
 *
 * Menace
 * When this creature enters or dies, exile another card from a graveyard. When you do, choose one —
 * • Remove X counters from target permanent, where X is the mana value of the exiled card.
 * • Target creature an opponent controls gets -X/-X until end of turn, where X is the mana value
 *   of the exiled card.
 *
 * Shape notes:
 *
 *  - **"enters or dies" is two triggered abilities**, not one — the corpus convention (Reputable
 *    Merchant, Thawbringer). They share [desecrate] because the payoff clause is identical.
 *  - **"When you do" is CR 603.12**, a genuine second stack object, so the exile is the
 *    [ReflexiveTriggerEffect]'s `action` and the modal is its `reflexiveEffect`. The exile is
 *    *mandatory* ("exile another card from a graveyard", no "may"), hence `optional = false`;
 *    `ReflexiveTriggerEffectExecutor.isActionFeasible` still suppresses the whole thing when every
 *    graveyard is empty, which is what stops the modal firing off an exile that never happened.
 *  - **The modal is chosen as the reflexive ability goes on the stack**, not on resolution — a
 *    top-level `ModalEffect` on a triggered ability is caught by `TriggerProcessor` (CR 603.3c),
 *    and the reflexive trigger is an ordinary triggered ability by the time it gets there. That is
 *    the correct timing here: the opponent sees which mode was picked while it is still
 *    respondable, and the mode's target only *becomes* a target at that moment (ward, "becomes the
 *    target of" triggers).
 *  - **"another"** excludes the Desecrator's own card, which matters only on the dies trigger —
 *    by then it is sitting in a graveyard itself. Entity ids are stable across zone changes, so
 *    `GameObjectFilter.Any.notSourceItself()` is exactly the printed word.
 *  - **X** is `StoredCardManaValue("exiledCard")` in both modes, read off the pipeline collection
 *    the action half stored; the reflexive trigger carries that pipeline forward
 *    (`ReflexiveAbilityTriggeredEvent.carriedPipeline`). Mode 1 removing "X counters" is
 *    [Effects.RemoveCounterOfAnyKind] with a dynamic count — the player picks which *kinds* come
 *    off, never whether, and the executor clamps the floor to what the permanent actually carries
 *    so a permanent with fewer than X counters simply loses all of them.
 */
private val desecrate: Effect = ReflexiveTriggerEffect(
    optional = false,
    action = Effects.Composite(
        listOf(
            GatherCardsEffect(
                source = CardSource.FromZone(
                    zone = Zone.GRAVEYARD,
                    player = Player.Each,
                    filter = GameObjectFilter.Any.notSourceItself()
                ),
                storeAs = "graveyardCards"
            ),
            SelectFromCollectionEffect(
                from = "graveyardCards",
                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                chooser = Chooser.Controller,
                storeSelected = "exiledCard",
                prompt = "Exile another card from a graveyard",
                showAllCards = true
            ),
            MoveCollectionEffect(
                from = "exiledCard",
                destination = CardDestination.ToZone(Zone.EXILE)
            )
        )
    ),
    reflexiveEffect = ModalEffect.chooseOne(
        Mode(
            effect = Effects.RemoveCounterOfAnyKind(
                target = EffectTarget.ContextTarget(0),
                count = DynamicAmount.StoredCardManaValue("exiledCard")
            ),
            targetRequirements = listOf(
                TargetObject(filter = TargetFilter.Permanent, id = "target permanent")
            ),
            description = "Remove X counters from target permanent, " +
                "where X is the mana value of the exiled card"
        ),
        Mode(
            effect = Effects.ModifyStats(
                DynamicAmount.Multiply(DynamicAmount.StoredCardManaValue("exiledCard"), -1),
                DynamicAmount.Multiply(DynamicAmount.StoredCardManaValue("exiledCard"), -1),
                EffectTarget.ContextTarget(0)
            ),
            targetRequirements = listOf(
                TargetObject(
                    filter = TargetFilter.Creature.opponentControls(),
                    id = "target creature an opponent controls"
                )
            ),
            description = "Target creature an opponent controls gets -X/-X until end of turn, " +
                "where X is the mana value of the exiled card"
        )
    ),
    descriptionOverride = "Exile another card from a graveyard. When you do, choose one — " +
        "remove X counters from target permanent, or target creature an opponent controls gets " +
        "-X/-X until end of turn, where X is the mana value of the exiled card"
)

val CemeteryDesecrator = card("Cemetery Desecrator") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 4
    toughness = 4
    oracleText = "Menace\n" +
        "When this creature enters or dies, exile another card from a graveyard. " +
        "When you do, choose one —\n" +
        "• Remove X counters from target permanent, where X is the mana value of the exiled card.\n" +
        "• Target creature an opponent controls gets -X/-X until end of turn, where X is the " +
        "mana value of the exiled card."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = desecrate
    }

    triggeredAbility {
        trigger = Triggers.Dies
        effect = desecrate
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "100"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48da33b1-d59c-43f1-8e55-480096b674e5.jpg?1783924869"
    }
}
