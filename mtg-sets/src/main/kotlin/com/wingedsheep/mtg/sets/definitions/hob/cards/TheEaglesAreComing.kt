package com.wingedsheep.mtg.sets.definitions.hob.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.effects.DelayedTriggerTiming
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.predicates.ControllerPredicate
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Eagles Are Coming!
 * {1}{W}
 * Instant
 * Kicker {2}{W}{W}
 *
 * Choose target creature you own. If this spell was kicked, instead choose any number of target
 * creatures you own. Return each chosen creature to your hand. At the beginning of the next upkeep,
 * create a 4/4 white Bird Soldier creature token with flying for each creature returned to your
 * hand this way.
 *
 * The kicked cast swaps only the *targeting* (one creature → any number), never the effect, so this
 * card sets `kickerTarget` without a `kickerEffect` — the engine falls back to the printed
 * `spellEffect` when no kicked variant is defined. That works because the effect reads
 * [CardSource.ChosenTargets], which enumerates however many targets were actually announced. The
 * shape is Divine Resilience's (FDN), minus the second effect.
 *
 * "Creature you **own**" is ownership, not control ([ControllerPredicate.OwnedByYou]): a creature of
 * yours that an opponent has stolen is a legal target, and returning it puts it in *your* hand,
 * since a card always returns to its owner's hand.
 *
 * The token count is `moveTracked`'s record of what actually reached the hand, not the announced
 * target list — a target that became illegal is removed on resolution and must not be counted.
 * [DynamicAmount.DistinctEntitiesInCollections] counts entity ids without looking them up in state,
 * which is what makes the card's ruling work: a *token* creature returned to hand is counted even
 * though it ceases to exist (CR 111.7) before the delayed trigger fires.
 *
 * That count has to be frozen when the spell resolves, because the pipeline holding the collection
 * is gone by the next upkeep — `CreateDelayedTriggerExecutor` does that for the pipeline-scoped
 * amounts it finds in a scheduled `CreateTokenEffect`.
 *
 * "The **next** upkeep" is any player's, not specifically yours, so the delayed trigger carries no
 * `fireOnPlayer` and uses [DelayedTriggerTiming.CURRENT_TURN_OR_LATER] — the next upkeep step to
 * begin, which on your own turn is your opponent's.
 */
val TheEaglesAreComing = card("The Eagles Are Coming!") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Kicker {2}{W}{W} (You may pay an additional {2}{W}{W} as you cast this spell.)\n" +
        "Choose target creature you own. If this spell was kicked, instead choose any number of " +
        "target creatures you own. Return each chosen creature to your hand. At the beginning of " +
        "the next upkeep, create a 4/4 white Bird Soldier creature token with flying for each " +
        "creature returned to your hand this way."

    keywordAbility(KeywordAbility.kicker("{2}{W}{W}"))

    spell {
        val creatureYouOwn = TargetFilter(
            GameObjectFilter.Creature.withControllerPredicate(ControllerPredicate.OwnedByYou)
        )

        // Unkicked: exactly one target creature you own.
        target = TargetObject(filter = creatureYouOwn)

        // Kicked: any number of target creatures you own. No kickerEffect — the pipeline below
        // reads whatever was announced, so both modes share one effect.
        kickerTarget = TargetObject(unlimited = true, filter = creatureYouOwn)

        effect = Effects.Pipeline {
            val chosen = gather(CardSource.ChosenTargets, name = "chosen")
            val returned = moveTracked(
                chosen,
                CardDestination.ToZone(Zone.HAND, Player.You),
                name = "returned"
            )
            run(
                CreateDelayedTriggerEffect(
                    step = Step.UPKEEP,
                    timing = DelayedTriggerTiming.CURRENT_TURN_OR_LATER,
                    effect = Effects.CreateToken(
                        count = DynamicAmount.DistinctEntitiesInCollections(listOf(returned.key)),
                        power = 4,
                        toughness = 4,
                        colors = setOf(Color.WHITE),
                        creatureTypes = setOf("Bird", "Soldier"),
                        keywords = setOf(Keyword.FLYING),
                        imageUri = "https://cards.scryfall.io/normal/front/9/e/9ed2d2f3-e1b0-41e0-a29d-31624e5e9004.jpg?1785524352"
                    )
                )
            )
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "12"
        artist = "Zezhou Chen"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0bda1b62-47fc-42c2-a841-ccad8ea0db48.jpg?1784376936"
        ruling("2026-06-29", "The kicker ability doesn't let you pay a kicker cost more than once.")
        ruling("2026-06-29", "Any creature tokens that were returned to your hand are counted by the delayed triggered ability. You'll get a Bird Soldier token for each of them.")
        ruling("2026-06-29", "If a spell's kicker cost was paid, the spell is \"kicked.\"")
        ruling("2026-06-29", "If you copy a kicked spell on the stack, the copy is also kicked.")
    }
}
