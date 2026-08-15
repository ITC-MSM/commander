package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import com.wingedsheep.sdk.scripting.effects.LoseLifeEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dai Li Agents
 * {3}{B}{G}
 * Creature — Human Soldier
 * 3/4
 *
 * When this creature enters, earthbend 1, then earthbend 1. (To earthbend 1, target land you
 * control becomes a 0/0 creature with haste that's still a land. Put a +1/+1 counter on it. When
 * it dies or is exiled, return it to the battlefield tapped.)
 * Whenever this creature attacks, each opponent loses X life and you gain X life, where X is the
 * number of creatures you control with +1/+1 counters on them.
 *
 * The ETB is two sequential earthbend-1 keyword actions, each with its own "target land you
 * control" slot (they may be the same land or two different lands; both targets are chosen when the
 * ability is put on the stack). Earthbend is composed from existing primitives via
 * [Effects.Earthbend] (animate land + haste + a +1/+1 counter + return-tapped self-triggers),
 * mirroring Earthbending Lesson / Badgermole.
 *
 * The attack drain mirrors Valley Rotcaller: X is an [DynamicAmount.AggregateBattlefield] count of
 * creatures you control that have +1/+1 counters on them (this creature included if it has one),
 * paid out as each opponent losing X and the controller gaining X.
 */
val DaiLiAgents = card("Dai Li Agents") {
    manaCost = "{3}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 4
    oracleText = "When this creature enters, earthbend 1, then earthbend 1. (To earthbend 1, " +
        "target land you control becomes a 0/0 creature with haste that's still a land. Put a " +
        "+1/+1 counter on it. When it dies or is exiled, return it to the battlefield tapped.)\n" +
        "Whenever this creature attacks, each opponent loses X life and you gain X life, where X " +
        "is the number of creatures you control with +1/+1 counters on them."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val firstLand = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        val secondLand = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = Effects.Composite(
            listOf(
                Effects.Earthbend(1, firstLand),
                Effects.Earthbend(1, secondLand),
            )
        )
        description = "When this creature enters, earthbend 1, then earthbend 1."
    }

    val xAmount = DynamicAmount.AggregateBattlefield(
        player = Player.You,
        filter = GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE),
    )

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            listOf(
                LoseLifeEffect(xAmount, EffectTarget.PlayerRef(Player.EachOpponent)),
                GainLifeEffect(xAmount, EffectTarget.Controller),
            )
        )
        description = "Whenever this creature attacks, each opponent loses X life and you gain X " +
            "life, where X is the number of creatures you control with +1/+1 counters on them."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "214"
        artist = "Eduardo Francisco"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9ddb8715-341b-400a-b6eb-f3f90518157c.jpg?1764121532"
    }
}
