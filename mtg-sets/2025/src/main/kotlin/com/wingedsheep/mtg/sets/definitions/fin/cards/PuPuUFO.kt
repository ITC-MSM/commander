package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * PuPu UFO
 * {2}
 * Artifact Creature — Construct Alien
 * 0/4
 * Flying
 * {T}: You may put a land card from your hand onto the battlefield.
 * {3}: Until end of turn, this creature's base power becomes equal to the number of Towns
 *   you control.
 *
 * The land drop is the standard `Gather → Select(up to 1) → Move` pipeline
 * ([Patterns.Hand.putFromHand]); the `ChooseUpTo(1)` selection models the "you may". The
 * pump is a Layer 7b set-base-power effect ([Effects.SetBasePower]) lasting until end of turn,
 * with the value recomputed at resolution as the count of Town lands the controller has in play.
 */
val PuPuUFO = card("PuPu UFO") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Construct Alien"
    power = 0
    toughness = 4
    oracleText = "Flying\n" +
        "{T}: You may put a land card from your hand onto the battlefield.\n" +
        "{3}: Until end of turn, this creature's base power becomes equal to the number of Towns you control."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Tap
        effect = Patterns.Hand.putFromHand(filter = GameObjectFilter.Land)
    }

    activatedAbility {
        cost = Costs.Mana("{3}")
        effect = Effects.SetBasePower(
            target = EffectTarget.Self,
            power = DynamicAmount.Count(
                player = Player.You,
                zone = Zone.BATTLEFIELD,
                filter = GameObjectFilter.Land.withSubtype("Town")
            ),
            duration = Duration.EndOfTurn
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "266"
        artist = "Racrufi"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/989b52f7-d8a5-4488-9a5d-f14a1d48686d.jpg?1748706782"
    }
}
