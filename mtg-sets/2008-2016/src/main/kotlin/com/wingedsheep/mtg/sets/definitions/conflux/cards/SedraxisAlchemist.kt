package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Sedraxis Alchemist
 * {2}{B}
 * Creature — Zombie Wizard
 * 2 / 2
 * When this creature enters, if you control a blue permanent, return target nonland permanent to its owner's hand.
 *
 * Conflux's "if you control a <colour> permanent" clause is an ordinary intervening-"if"
 * (CR 603.4): checked when the Alchemist enters and rechecked on resolution, so losing the last
 * blue permanent in response fizzles the bounce. [Conditions.YouControl] over
 * `Permanent.withColor(BLUE)` is the existential — the bare noun "permanent" is not narrowed to
 * creatures. The bounce is [Effects.ReturnToHand], which routes to the card's *owner's* hand on its
 * own, over the prebuilt [Targets.NonlandPermanent].
 */
val SedraxisAlchemist = card("Sedraxis Alchemist") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, if you control a blue permanent, return target nonland permanent to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.YouControl(GameObjectFilter.Permanent.withColor(Color.BLUE))
        val bounced = target("target", Targets.NonlandPermanent)
        effect = Effects.ReturnToHand(bounced)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "Karl Kopinski"
        flavorText = "The problem with a liquid that can dissolve anything is finding something to carry it in."
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7fbbc5d7-86d0-4e09-b37c-e40eb88f3f33.jpg"
    }
}
