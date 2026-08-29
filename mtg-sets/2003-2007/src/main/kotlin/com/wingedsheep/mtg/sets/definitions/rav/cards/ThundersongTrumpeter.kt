package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thundersong Trumpeter
 * {R}{W}
 * Creature — Human Soldier
 * 2/1
 * {T}: Target creature can't attack or block this turn.
 *
 * "Can't attack or block" is two restrictions, not one: [Effects.CantAttack] is read by the
 * combat-tax rules and [Effects.CantBlock] by the block-legality rules, so both must be applied
 * to the same target.
 */
val ThundersongTrumpeter = card("Thundersong Trumpeter") {
    manaCost = "{R}{W}"
    colorIdentity = "WR"
    typeLine = "Creature — Human Soldier"
    oracleText = "{T}: Target creature can't attack or block this turn."
    power = 2
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        val t = target("target creature", Targets.Creature)
        effect = Effects.Composite(
            Effects.CantAttack(t),
            Effects.CantBlock(t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "235"
        artist = "Michael Sutfin"
        flavorText = "\"Hear that? Those notes mean we've arrived at Sunhome! Let our allies' hearts soar and our enemies' hearts shatter at the sound!\"\n—Klattic, Boros legionnaire"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/7410c546-745c-469f-b3b8-eafb69391600.jpg"
    }
}
