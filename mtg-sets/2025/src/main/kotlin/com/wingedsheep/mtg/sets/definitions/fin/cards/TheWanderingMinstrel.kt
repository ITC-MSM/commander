package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersUntapped
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * The Wandering Minstrel — {G}{U} Legendary Creature — Human Bard (1/3).
 *
 * - "Lands you control enter untapped." — a static [EntersUntapped] replacement effect consulted
 *   from the battlefield whenever a land the Minstrel's controller controls would enter tapped
 *   (the inverse of [com.wingedsheep.sdk.scripting.EntersTapped]). Per CR 614 ordering this
 *   overrides a land's own "enters tapped" replacement (controller chooses untapped) and a land
 *   merely put onto the battlefield tapped (no replacement) enters untapped.
 * - "The Minstrel's Ballad" — a begin-combat-on-your-turn trigger with an intervening-if
 *   ([triggerCondition], Rule 603.4): it both fails to trigger and fails to resolve unless you
 *   control five or more Towns at that moment.
 * - The activated pump locks X (number of Towns you control) once as it resolves, since
 *   [Effects.ModifyStats] with a [DynamicAmount] evaluates to a fixed +X/+X floating effect.
 */
val TheWanderingMinstrel = card("The Wandering Minstrel") {
    manaCost = "{G}{U}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Creature — Human Bard"
    power = 1
    toughness = 3
    oracleText = "Lands you control enter untapped.\n" +
        "The Minstrel's Ballad — At the beginning of combat on your turn, if you control five or " +
        "more Towns, create a 2/2 Elemental creature token that's all colors.\n" +
        "{3}{W}{U}{B}{R}{G}: Other creatures you control get +X/+X until end of turn, where X is " +
        "the number of Towns you control."

    val townFilter = GameObjectFilter.Land.withSubtype("Town")
    val townCount = DynamicAmount.Count(Player.You, Zone.BATTLEFIELD, townFilter)

    // Lands you control enter untapped.
    replacementEffect(
        EntersUntapped(
            appliesTo = EventPattern.ZoneChangeEvent(
                filter = GameObjectFilter.Land.youControl(),
                to = Zone.BATTLEFIELD,
            )
        )
    )

    // The Minstrel's Ballad — At the beginning of combat on your turn, if you control five or
    // more Towns, create a 2/2 Elemental creature token that's all colors.
    triggeredAbility {
        trigger = Triggers.BeginCombat
        triggerCondition = Conditions.YouControlAtLeast(5, townFilter)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE, Color.BLUE, Color.BLACK, Color.RED, Color.GREEN),
            creatureTypes = setOf("Elemental"),
            imageUri = "https://cards.scryfall.io/normal/front/f/e/fe592a5c-5e6e-40ed-8818-f4651bcf2fe8.jpg?1748704098",
        )
        description = "The Minstrel's Ballad — At the beginning of combat on your turn, if you " +
            "control five or more Towns, create a 2/2 Elemental creature token that's all colors."
    }

    // {3}{W}{U}{B}{R}{G}: Other creatures you control get +X/+X until end of turn, where X is the
    // number of Towns you control.
    activatedAbility {
        cost = Costs.Mana("{3}{W}{U}{B}{R}{G}")
        effect = Patterns.Group.modifyStatsForAll(townCount, townCount, GroupFilter.OtherCreaturesYouControl)
        description = "Other creatures you control get +X/+X until end of turn, where X is the number of Towns you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "249"
        artist = "Thanh Tuấn"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77bc419d-ff69-4e7c-afe6-faca383a5ed7.jpg?1748706723"
    }
}
