package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vaevictis Asmadi
 * {2}{B}{B}{R}{R}{G}{G}
 * Legendary Creature — Elder Dragon
 * 7/7
 *
 * Flying
 * At the beginning of your upkeep, sacrifice Vaevictis Asmadi unless you pay {B}{R}{G}.
 * {B}: Vaevictis Asmadi gets +1/+0 until end of turn.
 * {R}: Vaevictis Asmadi gets +1/+0 until end of turn.
 * {G}: Vaevictis Asmadi gets +1/+0 until end of turn.
 *
 * The upkeep tax is CR 118.3's "unless": [PayOrSufferEffect] asks the controller to
 * pay on resolution and sacrifices the dragon when they decline or cannot.
 */
val VaevictisAsmadi = card("Vaevictis Asmadi") {
    manaCost = "{2}{B}{B}{R}{R}{G}{G}"
    colorIdentity = "BGR"
    typeLine = "Legendary Creature — Elder Dragon"
    power = 7
    toughness = 7
    oracleText = "Flying\n" +
        "At the beginning of your upkeep, sacrifice Vaevictis Asmadi unless you pay {B}{R}{G}.\n" +
        "{B}: Vaevictis Asmadi gets +1/+0 until end of turn.\n" +
        "{R}: Vaevictis Asmadi gets +1/+0 until end of turn.\n" +
        "{G}: Vaevictis Asmadi gets +1/+0 until end of turn."

    keywords(Keyword.FLYING)
    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = PayOrSufferEffect(cost = Costs.pay.Mana("{B}{R}{G}"), suffer = SacrificeSelfEffect)
    }

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{R}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{G}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "269"
        artist = "Andi Rusu"
        imageUri = "https://cards.scryfall.io/normal/front/2/2/22ea73ec-1325-4437-a23f-dcda1767c713.jpg?1783948030"
    }
}
