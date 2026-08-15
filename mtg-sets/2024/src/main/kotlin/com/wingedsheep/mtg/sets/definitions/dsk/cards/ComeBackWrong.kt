package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Come Back Wrong
 * {2}{B}
 * Sorcery
 * Destroy target creature. If a creature card is put into a graveyard this way, return it to the
 * battlefield under your control. Sacrifice it at the beginning of your next end step.
 *
 * Substituted into this group in place of Valgavoth's Onslaught, whose "manifest dread X times,
 * then put X +1/+1 counters on each of those creatures" needs a count-driven (dynamic-X) repeat
 * loop with cross-iteration accumulation of the manifested creatures — an engine feature that does
 * not yet exist (`add-feature` territory), so it was skipped rather than approximated.
 *
 * Pipeline (the Zero Point Ballad / Kheru Lich Lord shape):
 *   1. `GatherCards(ChosenTargets)` references the targeted creature.
 *   2. `MoveCollection(..., MoveType.Destroy, storeMovedAs = "died")` destroys it via the standard
 *      path; an indestructible target, or a token (which ceases to exist), drops out of `"died"` —
 *      that's the "if a creature card is put into a graveyard this way" gate.
 *   3. Select the nontoken creature card from `"died"` (SelectAll — no choice) and
 *      `MoveCollection` it to the battlefield under your control, recapturing it as
 *      `"comeBackWrong"`. With nothing in `"died"`, the select/move are no-ops.
 *   4. `CreateDelayedTrigger` at the next end step sacrifices the reanimated permanent
 *      ([EffectTarget.PipelineTarget]).
 */
val ComeBackWrong = card("Come Back Wrong") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature. If a creature card is put into a graveyard this way, " +
        "return it to the battlefield under your control. Sacrifice it at the beginning of your " +
        "next end step."

    val reanimated = EffectTarget.PipelineTarget("comeBackWrong")

    spell {
        target("target creature", Targets.Creature)
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(source = CardSource.ChosenTargets, storeAs = "comeBackTargets"),
                MoveCollectionEffect(
                    from = "comeBackTargets",
                    destination = CardDestination.ToZone(Zone.GRAVEYARD),
                    moveType = MoveType.Destroy,
                    storeMovedAs = "died"
                ),
                // "If a creature card is put into a graveyard this way" — tokens cease to exist, so
                // only a nontoken creature card remains to be reanimated.
                SelectFromCollectionEffect(
                    from = "died",
                    selection = SelectionMode.All,
                    filter = GameObjectFilter.Creature.nontoken(),
                    storeSelected = "toReanimate"
                ),
                MoveCollectionEffect(
                    from = "toReanimate",
                    destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                    storeMovedAs = "comeBackWrong"
                ),
                CreateDelayedTriggerEffect(
                    step = Step.END,
                    effect = Effects.SacrificeTarget(reanimated)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "86"
        artist = "David Auden Nash"
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72ee3b45-aa4e-4c5b-a9e6-608bfbd93f8b.jpg?1726286171"
    }
}
