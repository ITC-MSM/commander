package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Trumpeting Carnosaur
 * {4}{R}{R}
 * Creature — Dinosaur
 * 7/6
 * Trample
 * When this creature enters, discover 5.
 * {2}{R}, Discard this card: It deals 3 damage to target creature or planeswalker.
 */
val TrumpetingCarnosaur = card("Trumpeting Carnosaur") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dinosaur"
    power = 7
    toughness = 6
    oracleText = "Trample\nWhen this creature enters, discover 5.\n{2}{R}, Discard this card: It deals 3 damage to target creature or planeswalker."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Discover(5)
    }

    // {2}{R}, Discard this card (from hand): it deals 3 damage to target creature or planeswalker.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}"), Costs.DiscardSelf)
        val t = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.DealDamage(3, t, damageSource = EffectTarget.Self)
        activateFromZone = Zone.HAND
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "171"
        artist = "Lars Grant-West"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edc035ca-f0a3-4814-9405-d6dc6f048315.jpg?1782694473"
    }
}
