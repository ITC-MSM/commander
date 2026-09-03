package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Paleoloth
 * {4}{G}{G}
 * Creature — Beast
 * 5 / 5
 * Whenever another creature you control with power 5 or greater enters, you may return target creature card from your graveyard to your hand.
 *
 * The printed "another" is [TriggerBinding.OTHER] on a [Triggers.entersBattlefield] whose filter
 * carries the whole restriction — creature, power 5 or greater, controlled by you — so the power
 * test is read off projected state when the permanent enters rather than baked into a condition.
 * Paleoloth is itself a 5/5, which is exactly why the binding matters: under `SELF`/`ANY` it would
 * recur a card off its own arrival. The printed "you may" is `optional = true`
 * (a `Gate.MayDecide` around the recursion), and the target is the prebuilt
 * [Targets.CreatureCardInYourGraveyard] — owned by you, in the graveyard zone.
 */
val Paleoloth = card("Paleoloth") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 5
    oracleText = "Whenever another creature you control with power 5 or greater enters, you may return target creature card from your graveyard to your hand."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.powerAtLeast(5).youControl(),
            binding = TriggerBinding.OTHER
        )
        optional = true
        val recurred = target("target", Targets.CreatureCardInYourGraveyard)
        effect = Effects.ReturnToHand(recurred)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "88"
        artist = "Christopher Moeller"
        flavorText = "\"Gods do not sleep soundly in the earth's embrace.\" —Mayael the Anima"
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b83ad801-44e7-48d0-9f34-0d10536bb4dc.jpg"
    }
}
