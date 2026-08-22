package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.OnEnterRunEffect
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Frankenstein's Monster
 * {X}{B}{B}
 * Creature — Zombie
 * 0/1
 * As this creature enters, exile X creature cards from your graveyard. If you can't, put this
 * creature into its owner's graveyard instead of onto the battlefield.
 * For each creature card exiled this way, this creature enters with a +2/+0, +1/+1, or +0/+2
 * counter on it.
 *
 * The counters are the interesting half. "For each card exiled, choose one of three" is X
 * independent choices among the same three options, all landing on this creature — which is
 * exactly a repeatable modal: `ModalEffect` with `allowRepeat` and both the dynamic ceiling and
 * the dynamic floor pinned to X, so the player makes exactly X choices and may make the same one
 * every time. Iterating the exiled pile instead would fight the pipeline, since inside a
 * collection iteration `EffectTarget.Self` means the *iterated card*, not the Monster.
 *
 * +2/+0 and +0/+2 are new counter kinds. They are genuinely distinct from two +1/+0 counters
 * (CR 122.1a), so they are their own enum members with their own P/T arithmetic rather than being
 * approximated by doubling an existing kind.
 *
 * **Known divergence.** "If you can't, put this creature into its owner's graveyard instead of
 * onto the battlefield" is a true entry replacement, but `OnEnterRunEffect` runs just *after* the
 * permanent is on the battlefield, so the Monster is modelled as entering and then being put into
 * the graveyard when the graveyard can't pay. The observable difference is that its entry has
 * already happened. Nothing in this set notices, and X is chosen by the caster, so the case only
 * arises from a deliberately overpaid X.
 */
val FrankensteinsMonster = card("Frankenstein's Monster") {
    manaCost = "{X}{B}{B}"
    typeLine = "Creature — Zombie"
    power = 0
    toughness = 1
    oracleText = "As this creature enters, exile X creature cards from your graveyard. If you " +
        "can't, put this creature into its owner's graveyard instead of onto the battlefield.\n" +
        "For each creature card exiled this way, this creature enters with a +2/+0, +1/+1, or " +
        "+0/+2 counter on it."

    replacementEffect(
        OnEnterRunEffect(
            Effects.Composite(
                GatherCardsEffect(
                    source = CardSource.FromZone(
                        zone = Zone.GRAVEYARD,
                        player = Player.You,
                        filter = GameObjectFilter.Creature,
                    ),
                    storeAs = "fmCandidates",
                ),
                SelectFromCollectionEffect(
                    from = "fmCandidates",
                    selection = SelectionMode.ChooseExactly(DynamicAmount.XValue),
                    storeSelected = "fmExiled",
                ),
                MoveCollectionEffect(
                    from = "fmExiled",
                    destination = CardDestination.ToZone(Zone.EXILE),
                ),
                ModalEffect(
                    modes = listOf(
                        Mode(
                            description = "Put a +2/+0 counter on this creature",
                            effect = Effects.AddCounters(Counters.PLUS_TWO_PLUS_ZERO, 1, EffectTarget.Self),
                        ),
                        Mode(
                            description = "Put a +1/+1 counter on this creature",
                            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                        ),
                        Mode(
                            description = "Put a +0/+2 counter on this creature",
                            effect = Effects.AddCounters(Counters.PLUS_ZERO_PLUS_TWO, 1, EffectTarget.Self),
                        ),
                    ),
                    allowRepeat = true,
                    countsAsModalSpell = false,
                    dynamicChooseCount = DynamicAmount.XValue,
                    dynamicMinChooseCount = DynamicAmount.XValue,
                ),
            )
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "45"
        artist = "Anson Maddocks"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f99894d-5ece-44f1-acce-474494ae2084.jpg?1783947939"
    }
}
