package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Ore Gorger
 * {3}{R}{R}
 * Creature — Spirit
 * 3/1
 * Whenever you cast a Spirit or Arcane spell, you may destroy target nonbasic land.
 *
 * The red half of the CHK "Whenever you cast a Spirit or Arcane spell" cycle: the shared
 * [Triggers.youCastSpell] over a homogeneous OR of the two subtype filters, binding `ANY`.
 *
 * The printed "you may" is the builder's `optional = true`, which lowers to a `Gate.MayDecide`
 * around the destroy — the model has no separate optional flag. The target is still chosen when
 * the trigger goes on the stack (a "you may" gates the *action*, not the targeting, CR 603.3d),
 * so an ability with no legal nonbasic land in play never goes on the stack at all.
 *
 * [TargetFilter.NonbasicLand] is `IsLand` plus `Not(IsBasicLand)` — a supertype test, so a
 * nonbasic land printed with a basic land type (a Kamigawa legendary land, a shockland) is a
 * legal target while a real basic never is.
 */
val OreGorger = card("Ore Gorger") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a Spirit or Arcane spell, you may destroy target nonbasic land."
    power = 3
    toughness = 1

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        val land = target("target", TargetPermanent(filter = TargetFilter.NonbasicLand))
        effect = Effects.Destroy(land)
        optional = true
        description = "Whenever you cast a Spirit or Arcane spell, you may destroy target " +
            "nonbasic land."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "182"
        artist = "rk post"
        flavorText = "\"We've stumbled upon a network of caves not on our maps. We can only hope " +
            "it is safe to spend the night.\"\n" +
            "—Lost Battalion, message to General Takeno"
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ecc8bbb9-d6a1-474b-8f34-594b5a9d4178.jpg?1783944296"
    }
}
