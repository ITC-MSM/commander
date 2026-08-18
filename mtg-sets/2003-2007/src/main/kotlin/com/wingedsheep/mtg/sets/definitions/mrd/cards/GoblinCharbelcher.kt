package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Goblin Charbelcher — Mirrodin #176 (canonical printing)
 * {4} · Artifact
 *
 * {3}, {T}: Reveal cards from the top of your library until you reveal a land card. This artifact
 * deals damage equal to the number of nonland cards revealed this way to any target. If the
 * revealed land card was a Mountain, this artifact deals double that damage instead. Put the
 * revealed cards on the bottom of your library in any order.
 *
 * The Erratic Explosion shape with the partition inverted: [GatherUntilMatchEffect] walks the
 * library until it hits a *land* rather than a nonland, so the "revealed this way" pile is every
 * card seen and `landCard` holds the single stopper. A [FilterCollectionEffect] splits the
 * nonlands back out because the damage counts them, not the whole reveal — and the two differ by
 * exactly one whenever a land was actually found.
 *
 * The Mountain clause is a [DynamicAmount.Conditional] over that stopper rather than a separate
 * effect branch: "double that damage instead" replaces the amount, so it has to be one damage
 * event. `withSubtype("Mountain")` is the land *type*, not the card name — a Sacred Foundry or a
 * Mountain-typed Blinkmoth Nexus doubles the damage just as a basic Mountain does.
 *
 * Running the library out is the same code path with no stopper: `landCard` stays empty, the
 * condition is false, and every card revealed was nonland, which is the 2016-06-08 ruling
 * ("If you reveal no land cards, Goblin Charbelcher deals damage equal to the number of cards
 * revealed"). The target is chosen at activation, before anything is revealed.
 */
val GoblinCharbelcher = card("Goblin Charbelcher") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{3}, {T}: Reveal cards from the top of your library until you reveal a land card. " +
        "This artifact deals damage equal to the number of nonland cards revealed this way to any target. " +
        "If the revealed land card was a Mountain, this artifact deals double that damage instead. " +
        "Put the revealed cards on the bottom of your library in any order."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap)
        val victim = target("any target", AnyTarget())

        val nonlandsRevealed = DynamicAmounts.distinctEntitiesIn("nonlands")

        effect = Effects.Composite(
            listOf(
                GatherUntilMatchEffect(
                    filter = GameObjectFilter.Land,
                    storeMatch = "landCard",
                    storeRevealed = "revealed"
                ),
                RevealCollectionEffect(from = "revealed"),
                FilterCollectionEffect(
                    from = "revealed",
                    filter = CollectionFilter.MatchesFilter(GameObjectFilter.Nonland),
                    storeMatching = "nonlands"
                ),
                Effects.DealDamage(
                    amount = DynamicAmount.Conditional(
                        condition = Conditions.CollectionContainsMatch(
                            collection = "landCard",
                            filter = GameObjectFilter.Land.withSubtype("Mountain")
                        ),
                        ifTrue = DynamicAmount.Multiply(nonlandsRevealed, 2),
                        ifFalse = nonlandsRevealed
                    ),
                    target = victim
                ),
                MoveCollectionEffect(
                    from = "revealed",
                    destination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
                    order = CardOrder.ControllerChooses
                )
            )
        )
        description = "{3}, {T}: Reveal cards from the top of your library until you reveal a land card. " +
            "This artifact deals damage equal to the number of nonland cards revealed this way to any target. " +
            "If the revealed land card was a Mountain, this artifact deals double that damage instead. " +
            "Put the revealed cards on the bottom of your library in any order."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "176"
        artist = "Stephen Tappin"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6c37fc1-4842-4bc5-93ed-83fff9e420f2.jpg?1783944520"
        ruling("2016-06-08", "You must choose a target for Goblin Charbelcher's ability as you activate it, before you reveal any cards.")
        ruling("2016-06-08", "If you reveal no land cards, Goblin Charbelcher deals damage equal to the number of cards revealed, and then you may order your library as you like.")
    }
}
