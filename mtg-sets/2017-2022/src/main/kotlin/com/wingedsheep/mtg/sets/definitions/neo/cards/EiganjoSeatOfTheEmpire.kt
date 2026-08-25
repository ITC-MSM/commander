package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Eiganjo, Seat of the Empire — Kamigawa: Neon Dynasty #268 (canonical printing)
 * Legendary Land · Rare
 *
 * {T}: Add {W}.
 * Channel — {2}{W}, Discard this card: It deals 4 damage to target attacking or blocking
 * creature. This ability costs {1} less to activate for each legendary creature you control.
 *
 * One of the five NEO channel lands; see [OtawaraSoaringCity] for the shape they share.
 *
 * "It deals 4 damage" — the *land card* is the source, not a permanent: the ability is activated
 * from hand and the card is in the graveyard by the time it resolves. `Effects.DealDamage` with
 * the ability's own source is exactly that.
 */
val EiganjoSeatOfTheEmpire = card("Eiganjo, Seat of the Empire") {
    typeLine = "Legendary Land"
    colorIdentity = "W"
    oracleText = "{T}: Add {W}.\n" +
        "Channel — {2}{W}, Discard this card: It deals 4 damage to target attacking or blocking " +
        "creature. This ability costs {1} less to activate for each legendary creature you control."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
    }

    // Channel — {2}{W}, Discard this card (from hand): 4 damage to an attacking/blocking creature.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        genericCostReduction = DynamicAmounts.legendaryCreaturesYouControl()
        val t = target(
            "target attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature)
        )
        effect = Effects.DealDamage(4, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "268"
        artist = "Julian Kok Joon Wen"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c375a022-5b57-496d-a802-e4ea8376e9e4.jpg?1783923818"
    }
}
