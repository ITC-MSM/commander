package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Centaur Archer
 * {1}{R}{G}
 * Creature — Centaur Archer
 * 3/2
 *
 * {T}: This creature deals 1 damage to target creature with flying.
 *
 * Grapeshot Catapult's shape: the "with flying" restriction is a targeting predicate, so it rides
 * [Targets.CreatureWithKeyword] rather than a condition on the effect. The damage source stays the
 * ability's own source, which is [Effects.DealDamage]'s default.
 */
val CentaurArcher = card("Centaur Archer") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Centaur Archer"
    power = 3
    toughness = 2
    oracleText = "{T}: This creature deals 1 damage to target creature with flying."

    activatedAbility {
        cost = Costs.Tap
        val t = target("target", Targets.CreatureWithKeyword(Keyword.FLYING))
        effect = Effects.DealDamage(1, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "282"
        artist = "Melissa A. Benson"
        flavorText = "\"Centaurs will kill our Aesthir if they can; they've always been enemies. Destroy the horse-people on sight.\"\n—Arna Kennerüd, Skyknight"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e275c295-72da-4a86-82c6-cfd75b38b19c.jpg"
    }
}
