package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Firemane Angel
 * {3}{R}{W}{W}
 * Creature — Angel
 * 4/3
 *
 * Flying, first strike
 * At the beginning of your upkeep, if Firemane Angel is in your graveyard or on the battlefield,
 * you may gain 1 life.
 * {6}{R}{R}{W}{W}: Return this card from your graveyard to the battlefield.
 * Activate only during your upkeep.
 *
 * The life trigger is the Edgar Markov shape, and for the same two rules. CR 113.6b — "an ability
 * that states which zones it functions in functions only from those zones" — is
 * `triggerZones = {GRAVEYARD, BATTLEFIELD}`, which is what makes the upkeep trigger fire off a dead
 * Angel at all. The identical printed clause is *also* an intervening-"if" (CR 603.4), checked a
 * second time as the ability resolves, and that second check is [Conditions.SourceInZone]. The two
 * halves are what the 2017-11-17 ruling turns on: an Angel that dies or is reanimated during your
 * upkeep after the trigger goes on the stack is a new object in a new zone, and you gain no life.
 *
 * The recursion is the ordinary graveyard-activation shape (Grim Reminder's template):
 * `activateFromZone = Zone.GRAVEYARD` plus the your-turn/upkeep restriction pair, with the return
 * an explicit `fromZone = GRAVEYARD` move so a card that has already left cannot be moved twice.
 * Nothing links the two abilities — reanimating the Angel does not "use up" the life trigger, it
 * just moves the card out from under it.
 */
val FiremaneAngel = card("Firemane Angel") {
    manaCost = "{3}{R}{W}{W}"
    colorIdentity = "RW"
    typeLine = "Creature — Angel"
    power = 4
    toughness = 3
    oracleText = "Flying, first strike\n" +
        "At the beginning of your upkeep, if Firemane Angel is in your graveyard or on the " +
        "battlefield, you may gain 1 life.\n" +
        "{6}{R}{R}{W}{W}: Return this card from your graveyard to the battlefield. Activate only " +
        "during your upkeep."

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        triggerZones = setOf(Zone.BATTLEFIELD, Zone.GRAVEYARD)
        interveningIf = Conditions.SourceInZone(Zone.BATTLEFIELD, Zone.GRAVEYARD)
        effect = MayEffect(Effects.GainLife(1))
        description = "At the beginning of your upkeep, if Firemane Angel is in your graveyard or " +
            "on the battlefield, you may gain 1 life."
    }

    activatedAbility {
        cost = Costs.Mana("{6}{R}{R}{W}{W}")
        activateFromZone = Zone.GRAVEYARD
        effect = Effects.Move(EffectTarget.Self, Zone.BATTLEFIELD, fromZone = Zone.GRAVEYARD)
        restrictions = listOf(
            ActivationRestriction.All(
                ActivationRestriction.OnlyDuringYourTurn,
                ActivationRestriction.DuringStep(Step.UPKEEP)
            )
        )
        description = "Return this card from your graveyard to the battlefield. " +
            "Activate only during your upkeep."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "205"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/493730ab-c30d-4830-a188-0462426fc5bf.jpg?1783943621"
        ruling(
            "2017-11-17",
            "If Firemane Angel is put into your graveyard from the battlefield or returned from " +
                "your graveyard to the battlefield during your upkeep before its triggered " +
                "ability resolves, you won't gain 1 life."
        )
    }
}
