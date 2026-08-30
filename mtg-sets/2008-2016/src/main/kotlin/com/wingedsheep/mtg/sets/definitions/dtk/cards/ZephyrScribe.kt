package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zephyr Scribe
 * {2}{U}
 * Creature — Human Monk
 * 2 / 1
 *
 * {U}, {T}: Draw a card, then discard a card.
 * Whenever you cast a noncreature spell, untap this creature.
 *
 * "Draw a card, then discard a card" is the loot recipe ([Patterns.Hand.loot], draw 1 then the
 * gather/select/discard pipeline), and the untap trigger is a plain [Triggers.YouCastNoncreature] —
 * not prowess, which would add a +1/+1 the card doesn't print. Together they let each noncreature
 * spell refund the tap cost, so the ability can be activated once per spell.
 */
val ZephyrScribe = card("Zephyr Scribe") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Monk"
    power = 2
    toughness = 1
    oracleText = "{U}, {T}: Draw a card, then discard a card.\n" +
        "Whenever you cast a noncreature spell, untap this creature."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        effect = Patterns.Hand.loot()
    }

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "Lius Lasahido"
        flavorText = "\"Ojutai's rule has allowed Tarkir's monks to learn from the truly enlightened.\"\n—Sarkhan Vol"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d23ae0d2-0ca1-4095-8710-be5800e389cd.jpg?1783938601"
    }
}
