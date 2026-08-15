package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * The Boulder, Ready to Rumble
 * {3}{G}
 * Legendary Creature — Human Warrior Performer
 * 4/4
 *
 * Whenever The Boulder attacks, earthbend X, where X is the number of creatures you control with
 * power 4 or greater. (Target land you control becomes a 0/0 creature with haste that's still a
 * land. Put X +1/+1 counters on it. When it dies or is exiled, return it to the battlefield
 * tapped.)
 *
 * The attack trigger ([Triggers.Attacks]) targets a land you control and runs the set's
 * [Effects.Earthbend] over a dynamic amount: the count of creatures you control with power 4 or
 * greater ([DynamicAmounts.battlefield] + `powerAtLeast(4)`, `.count()`). The Boulder itself
 * (a 4/4) counts toward X while attacking.
 */
val TheBoulderReadyToRumble = card("The Boulder, Ready to Rumble") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Warrior Performer"
    power = 4
    toughness = 4
    oracleText = "Whenever The Boulder attacks, earthbend X, where X is the number of creatures " +
        "you control with power 4 or greater. (Target land you control becomes a 0/0 creature " +
        "with haste that's still a land. Put X +1/+1 counters on it. When it dies or is exiled, " +
        "return it to the battlefield tapped.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = Effects.Earthbend(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature.powerAtLeast(4)).count(),
            land,
        )
        description = "Whenever The Boulder attacks, earthbend X, where X is the number of " +
            "creatures you control with power 4 or greater."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "168"
        artist = "Thanh Tuấn"
        flavorText = "\"The Boulder's gonna win this in a landslide!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec27a466-5457-44c6-a842-1de7d3788d66.jpg?1764121147"
    }
}
