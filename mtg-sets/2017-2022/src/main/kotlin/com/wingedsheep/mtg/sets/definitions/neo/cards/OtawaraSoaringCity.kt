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
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Otawara, Soaring City — Kamigawa: Neon Dynasty #271 (canonical printing)
 * Legendary Land · Rare
 *
 * {T}: Add {U}.
 * Channel — {3}{U}, Discard this card: Return target artifact, creature, enchantment, or
 * planeswalker to its owner's hand. This ability costs {1} less to activate for each legendary
 * creature you control.
 *
 * The reference implementation of the five NEO channel lands (Boseiju, Eiganjo, Otawara,
 * Sokenzan, Takenuma). All five share one shape:
 *
 *  - **Channel is an ability *word*** (CR 207.2c) — italic flavour with no rules meaning and no
 *    entry of its own in the rules. So it is not modelled as a keyword: it lives in [oracleText]
 *    and nowhere else, exactly as Landfall and Magecraft do on their cards. The ability itself is
 *    an ordinary activated ability.
 *  - **Activated from hand**, paid for by discarding itself: `activateFromZone = Zone.HAND` plus
 *    [Costs.DiscardSelf] (the Steel Wrecking Ball shape).
 *  - **"Costs {1} less to activate for each legendary creature you control"** is
 *    [com.wingedsheep.sdk.scripting.ActivatedAbility.genericCostReduction], which shaves only the
 *    generic pips — so the coloured pip always survives and {3}{U} floors at {U}.
 *
 * Note the target is *any* artifact, creature, enchantment, or planeswalker — Otawara can bounce
 * your own permanent, unlike Boseiju, which is restricted to what an opponent controls.
 */
val OtawaraSoaringCity = card("Otawara, Soaring City") {
    typeLine = "Legendary Land"
    colorIdentity = "U"
    oracleText = "{T}: Add {U}.\n" +
        "Channel — {3}{U}, Discard this card: Return target artifact, creature, enchantment, or " +
        "planeswalker to its owner's hand. This ability costs {1} less to activate for each " +
        "legendary creature you control."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
    }

    // Channel — {3}{U}, Discard this card (from hand): bounce an artifact/creature/enchantment/PW.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        genericCostReduction = DynamicAmounts.legendaryCreaturesYouControl()
        val t = target(
            "target artifact, creature, enchantment, or planeswalker",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Artifact or
                        GameObjectFilter.Creature or
                        GameObjectFilter.Enchantment or
                        GameObjectFilter.Planeswalker
                )
            )
        )
        effect = Effects.ReturnToHand(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "271"
        artist = "Alayna Danner"
        imageUri = "https://cards.scryfall.io/normal/front/4/8/486d7edc-d983-41f0-8b78-c99aecd72996.jpg?1783923816"
    }
}
