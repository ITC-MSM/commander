package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Saradoc, Master of Buckland
 * {3}{W}
 * Legendary Creature — Halfling Citizen
 * 2/4
 *
 * Whenever Saradoc or another nontoken creature you control with power 2 or less enters,
 * create a 1/1 white Halfling creature token.
 * Tap two other untapped Halflings you control: Saradoc gets +2/+0 and gains lifelink until
 * end of turn.
 */
val SaradocMasterOfBuckland = card("Saradoc, Master of Buckland") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Halfling Citizen"
    power = 2
    toughness = 4
    oracleText = "Whenever Saradoc or another nontoken creature you control with power 2 or less enters, " +
        "create a 1/1 white Halfling creature token.\n" +
        "Tap two other untapped Halflings you control: Saradoc gets +2/+0 and gains lifelink until " +
        "end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Creature.nontoken().powerAtMost(2).youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Halfling"),
            imageUri = "https://cards.scryfall.io/normal/front/b/b/bbd9ff58-4d38-4d34-9dfc-6f788830297e.jpg?1777658851"
        )
    }

    activatedAbility {
        cost = Costs.TapPermanents(
            count = 2,
            filter = GameObjectFilter.Creature.withSubtype("Halfling").youControl(),
            excludeSelf = true
        )
        effect = Effects.ModifyStats(2, 0, EffectTarget.Self)
            .then(Effects.GrantKeyword(Keyword.LIFELINK, EffectTarget.Self))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "282"
        artist = "Sean Vo"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a65a1fb5-cc2c-4556-b109-05c3d966ded9.jpg?1719684252"
    }
}
