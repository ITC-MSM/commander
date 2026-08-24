package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kjeldoran Knight
 * {W}{W}
 * Creature — Human Knight
 * 1/1
 *
 * Banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)
 * {1}{W}: This creature gets +1/+0 until end of turn.
 * {W}{W}: This creature gets +0/+2 until end of turn.
 *
 * Banding is engine-live (`CombatDamageManager` consults it), so the bare `keywords(...)` entry is
 * real behaviour. The two pumps are plain mana-cost [Effects.ModifyStats] on [EffectTarget.Self] —
 * the same shape as the Order cycle, just split across power and toughness.
 */
val KjeldoranKnight = card("Kjeldoran Knight") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 1
    toughness = 1
    oracleText = "Banding (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)\n" +
        "{1}{W}: This creature gets +1/+0 until end of turn.\n" +
        "{W}{W}: This creature gets +0/+2 until end of turn."

    keywords(Keyword.BANDING)

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    activatedAbility {
        cost = Costs.Mana("{W}{W}")
        effect = Effects.ModifyStats(0, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "36"
        artist = "Ron Spencer"
        flavorText = "\"Those who do not ride the wind on Aesthir still command loyalty and respect.\"\n—Arna Kennerüd, Skyknight"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5b9db8f-93b5-44e3-9e2b-728c80dfbb37.jpg"
    }
}
