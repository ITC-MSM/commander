package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Geralf, Visionary Stitcher — Innistrad: Crimson Vow #61
 * {2}{U} · Legendary Creature — Human Wizard · Rare · 1/4
 * Artist: Bryan Sola
 *
 * Zombies you control have flying.
 * {U}, {T}, Sacrifice another nontoken creature: Create an X/X blue Zombie creature token, where X
 * is the sacrificed creature's toughness.
 *
 * Line 1 is a static lord grant ([GrantKeyword] over `Zombies you control`); Geralf is a Wizard, not
 * a Zombie, so no `excludeSelf` is needed. Line 2 is an activated ability whose additional cost
 * sacrifices *another nontoken* creature ([Costs.SacrificeAnother] excludes the source, plus a
 * `.nontoken()` filter). The token's X/X reads the sacrificed creature's toughness via
 * last-known information ([DynamicAmounts.sacrificedToughness], the Priest of Yawgmoth
 * [EntityReference.Sacrificed] LKI template), fed to both P and T of a dynamic token
 * ([Effects.CreateDynamicToken], the Soul Separator Zombie-token shape) — so a 0-toughness
 * sacrifice makes a 0/0 that dies as a state-based action.
 */
val GeralfVisionaryStitcher = card("Geralf, Visionary Stitcher") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Wizard"
    power = 1
    toughness = 4
    oracleText = "Zombies you control have flying.\n" +
        "{U}, {T}, Sacrifice another nontoken creature: Create an X/X blue Zombie creature token, " +
        "where X is the sacrificed creature's toughness."

    // Zombies you control have flying.
    staticAbility {
        ability = GrantKeyword(
            Keyword.FLYING,
            GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.ZOMBIE).youControl())
        )
    }

    // {U}, {T}, Sacrifice another nontoken creature: Create an X/X blue Zombie, X = its toughness.
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{U}"),
            Costs.Tap,
            Costs.SacrificeAnother(GameObjectFilter.Creature.nontoken())
        )
        effect = Effects.CreateDynamicToken(
            dynamicPower = DynamicAmounts.sacrificedToughness(),
            dynamicToughness = DynamicAmounts.sacrificedToughness(),
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Zombie")
        )
        description = "{U}, {T}, Sacrifice another nontoken creature: Create an X/X blue Zombie " +
            "creature token, where X is the sacrificed creature's toughness."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "61"
        artist = "Bryan Sola"
        flavorText = "\"Do you like it, Sister? Its dreadful visage was inspired by your own.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69890076-9cc4-434f-8618-63b00fdf4515.jpg?1783924892"
    }
}
