package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.Chooser
import com.wingedsheep.sdk.scripting.effects.CompositeEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.ForEachInCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ForEachInGroupEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerEffect
import com.wingedsheep.sdk.scripting.effects.GainControlEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantKeywordEffect
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.MoveToZoneEffect
import com.wingedsheep.sdk.scripting.effects.MoveType
import com.wingedsheep.sdk.scripting.effects.RemoveKeywordEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Effect patterns for bulk operations on filtered groups of permanents:
 * tap/untap, return, destroy, grant/remove keywords, modify stats, deal damage,
 * and gain control.
 */
object GroupPatterns {

    fun untapGroup(filter: GroupFilter = GroupFilter.AllCreatures): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = TapUntapEffect(EffectTarget.Self, tap = false)
        )

    fun tapAll(filter: GroupFilter): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = TapUntapEffect(EffectTarget.Self, tap = true)
        )

    fun returnAllToHand(filter: GroupFilter): CompositeEffect = CompositeEffect(listOf(
        GatherCardsEffect(
            source = CardSource.BattlefieldMatching(
                filter = filter.baseFilter,
                excludeSelf = filter.excludeSelf
            ),
            storeAs = "returnAllToHand_gathered"
        ),
        MoveCollectionEffect(
            from = "returnAllToHand_gathered",
            destination = CardDestination.ToZone(Zone.HAND)
        )
    ))

    fun destroyAll(filter: GroupFilter, noRegenerate: Boolean = false): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = MoveToZoneEffect(EffectTarget.Self, Zone.GRAVEYARD, byDestruction = true),
            noRegenerate = noRegenerate
        )

    fun destroyAllPipeline(
        filter: GameObjectFilter,
        noRegenerate: Boolean = false,
        storeDestroyedAs: String? = null,
        excludeTriggering: Boolean = false
    ): CompositeEffect = CompositeEffect(listOf(
        GatherCardsEffect(
            source = CardSource.BattlefieldMatching(filter = filter, excludeTriggering = excludeTriggering),
            storeAs = "destroyAll_gathered"
        ),
        MoveCollectionEffect(
            from = "destroyAll_gathered",
            destination = CardDestination.ToZone(Zone.GRAVEYARD),
            moveType = MoveType.Destroy,
            noRegenerate = noRegenerate,
            storeMovedAs = storeDestroyedAs
        )
    ))

    /**
     * "Each permanent matching [filter] is sacrificed by its controller." Gather → move via
     * [MoveType.Sacrifice] (emits `PermanentsSacrificedEvent`, routes to each card's owner
     * graveyard — CR 701.21). [excludeTriggering] excludes the source/triggering entity, for the
     * "except for ~" wording (Golgothian Sylex sparing itself).
     */
    fun sacrificeAllPipeline(
        filter: GameObjectFilter,
        excludeTriggering: Boolean = false
    ): CompositeEffect = CompositeEffect(listOf(
        GatherCardsEffect(
            source = CardSource.BattlefieldMatching(
                filter = filter,
                excludeSelf = excludeTriggering,
                excludeTriggering = excludeTriggering
            ),
            storeAs = "sacrificeAll_gathered"
        ),
        MoveCollectionEffect(
            from = "sacrificeAll_gathered",
            destination = CardDestination.ToZone(Zone.GRAVEYARD),
            moveType = MoveType.Sacrifice
        )
    ))

    /**
     * "Destroy the creature with the least power" (Drop of Honey). Gathers every creature tied
     * for the global minimum power, lets the controller pick one (auto-selected when there's a
     * single minimum, a choice on a tie — CR: "if two or more are tied, you choose"), and
     * destroys it.
     */
    fun destroyLeastPowerCreature(
        noRegenerate: Boolean = false
    ): CompositeEffect = CompositeEffect(listOf(
        GatherCardsEffect(
            source = CardSource.BattlefieldMatching(
                filter = GameObjectFilter.Creature.hasLeastPowerAmongAllCreatures()
            ),
            storeAs = "leastPower_gathered"
        ),
        SelectFromCollectionEffect(
            from = "leastPower_gathered",
            selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
            chooser = Chooser.Controller,
            storeSelected = "leastPower_chosen",
            useTargetingUI = true,
            prompt = "Choose a creature with the least power to destroy"
        ),
        MoveCollectionEffect(
            from = "leastPower_chosen",
            destination = CardDestination.ToZone(Zone.GRAVEYARD),
            moveType = MoveType.Destroy,
            noRegenerate = noRegenerate
        )
    ))

    /**
     * "Destroy all creatures blocking or blocked by it" (Abu Ja'far). Gathers the source's
     * last-known combat pairing (CR 509) — captured on the leaves-battlefield event because the
     * live cross-references are gone by the time a dies trigger resolves — and destroys those
     * still on the battlefield.
     */
    fun destroyCombatPairedWithSourcePipeline(
        noRegenerate: Boolean = false
    ): CompositeEffect = CompositeEffect(listOf(
        GatherCardsEffect(
            source = CardSource.LastKnownCombatPairedWithSource,
            storeAs = "combatPaired_gathered"
        ),
        MoveCollectionEffect(
            from = "combatPaired_gathered",
            destination = CardDestination.ToZone(Zone.GRAVEYARD),
            moveType = MoveType.Destroy,
            noRegenerate = noRegenerate
        )
    ))

    fun destroyAllAndAttachedPipeline(
        filter: GameObjectFilter,
        noRegenerate: Boolean = false
    ): CompositeEffect = CompositeEffect(listOf(
        GatherCardsEffect(
            source = CardSource.BattlefieldMatching(filter = filter, includeAttachments = true),
            storeAs = "destroyAllAttached_gathered"
        ),
        MoveCollectionEffect(
            from = "destroyAllAttached_gathered",
            destination = CardDestination.ToZone(Zone.GRAVEYARD),
            moveType = MoveType.Destroy,
            noRegenerate = noRegenerate
        )
    ))

    fun grantKeywordToAll(
        keyword: Keyword,
        filter: GroupFilter,
        duration: Duration = Duration.EndOfTurn
    ): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = GrantKeywordEffect(keyword.name, EffectTarget.Self, duration)
        )

    fun removeKeywordFromAll(
        keyword: Keyword,
        filter: GroupFilter,
        duration: Duration = Duration.EndOfTurn
    ): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = RemoveKeywordEffect(keyword.name, EffectTarget.Self, duration)
        )

    fun modifyStatsForAll(
        power: Int,
        toughness: Int,
        filter: GroupFilter,
        duration: Duration = Duration.EndOfTurn
    ): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = ModifyStatsEffect(power, toughness, EffectTarget.Self, duration)
        )

    fun modifyStatsForAll(
        power: DynamicAmount,
        toughness: DynamicAmount,
        filter: GroupFilter,
        duration: Duration = Duration.EndOfTurn
    ): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = ModifyStatsEffect(power, toughness, EffectTarget.Self, duration)
        )

    /**
     * Double the power and toughness of every permanent matching [filter] until [duration].
     *
     * Each affected creature gets +X/+Y where X is its power and Y its toughness *as the
     * effect begins to apply* — read per-entity from projected state via
     * [EntityReference.IterationEntity]. Because it resolves to a fixed +X/+Y modification,
     * the bonus is locked in when the effect resolves (it does not re-double as P/T later
     * changes), and negative power doubles correctly (a -2/3 creature gets -2/+0). This
     * applies as a power/toughness *modification* in layer 7 (the +N/+N sublayer), not a
     * "set" effect, matching the standard "double its power and toughness" ruling.
     *
     * The reusable shape behind every "double the power and toughness" card — e.g.
     * Roar of Endless Song, Unnatural Growth.
     */
    fun doublePowerAndToughnessForAll(
        filter: GroupFilter,
        duration: Duration = Duration.EndOfTurn
    ): ForEachEffect =
        modifyStatsForAll(
            power = DynamicAmount.EntityProperty(EntityReference.IterationEntity, EntityNumericProperty.Power),
            toughness = DynamicAmount.EntityProperty(EntityReference.IterationEntity, EntityNumericProperty.Toughness),
            filter = filter,
            duration = duration
        )

    fun dealDamageToAll(amount: Int, filter: GroupFilter): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = DealDamageEffect(amount, EffectTarget.Self)
        )

    fun dealDamageToAll(amount: DynamicAmount, filter: GroupFilter): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = DealDamageEffect(amount, EffectTarget.Self)
        )

    fun gainControlOfGroup(filter: GroupFilter = GroupFilter.AllCreatures, duration: Duration = Duration.EndOfTurn): ForEachEffect =
        ForEachInGroupEffect(
            filter = filter,
            effect = GainControlEffect(EffectTarget.Self, duration)
        )

    /**
     * "Each player returns a permanent they control to its owner's hand." Per-player
     * Gather → Select(1) → iterate the chosen card through MoveToZone(hand), in
     * active-player-first order.  Do not lower the last step to MoveCollection: a
     * Commander has an optional pre-move replacement for a hand move, and the
     * ForEach executor preserves this nested pause before continuing the outer
     * player loop.
     */
    fun eachPlayerReturnsPermanentToHand(): ForEachEffect = ForEachPlayerEffect(
        players = Player.ActivePlayerFirst,
        effects = listOf(
            GatherCardsEffect(
                source = CardSource.ControlledPermanents(Player.You),
                storeAs = "permanents"
            ),
            SelectFromCollectionEffect(
                from = "permanents",
                selection = SelectionMode.ChooseExactly(DynamicAmount.Fixed(1)),
                storeSelected = "chosen",
                prompt = "Choose a permanent to return to its owner's hand"
            ),
            ForEachInCollectionEffect(
                collection = "chosen",
                effect = MoveToZoneEffect(EffectTarget.Self, Zone.HAND)
            )
        )
    )
}
