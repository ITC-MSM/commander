package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Topan Ascetic
 * {2}{G}
 * Creature — Human Monk
 * 2 / 2
 * Tap an untapped creature you control: This creature gets +1/+1 until end of turn.
 *
 * The whole ability is a cost with no mana in it: [Costs.TapPermanents]`(count = 1, filter = `
 * [GameObjectFilter.Creature]`)` is the atomic "tap N untapped permanents you control" primitive,
 * and because the printed line says no "another" it leaves `excludeSelf` false — the Ascetic may
 * tap itself. The payoff is [Effects.ModifyStats]`(1, 1)` on [EffectTarget.Self], whose default
 * `Duration.EndOfTurn` is the printed "until end of turn".
 */
val TopanAscetic = card("Topan Ascetic") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Monk"
    power = 2
    toughness = 2
    oracleText = "Tap an untapped creature you control: This creature gets +1/+1 until end of turn."

    activatedAbility {
        cost = Costs.TapPermanents(count = 1, filter = GameObjectFilter.Creature)
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "151"
        artist = "Sal Villagran"
        flavorText = "Monks from Topa wander all of Bant, encouraging the Unbeholden to find their place in society through honorable combat."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d0952ed-efc7-4ff5-a233-e64c0f11119b.jpg"
    }
}
