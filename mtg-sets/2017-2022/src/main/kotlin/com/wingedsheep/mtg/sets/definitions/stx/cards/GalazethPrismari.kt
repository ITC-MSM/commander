package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.ManaRestriction
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Galazeth Prismari — Strixhaven: School of Mages #189 (canonical printing)
 * {2}{U}{R} · Legendary Creature — Elder Dragon · 3/4
 *
 * Flying
 * When Galazeth Prismari enters, create a Treasure token.
 * Artifacts you control have "{T}: Add one mana of any color. Spend this mana only to cast an instant or sorcery spell."
 *
 * The ETB is [Effects.CreateTreasure]. The granted ability is the Gemhide Sliver shape — a
 * [GrantActivatedAbility] static handing every artifact you control a tap mana ability — with
 * [Effects.AddManaOfChoice] carrying [ManaRestriction.InstantOrSorceryOnly], the Vodalian Arcanist
 * restriction. The Treasure the ETB makes is itself an artifact, so it gains the untap-free version
 * of its own ability alongside the printed sacrifice one.
 */
val GalazethPrismari = card("Galazeth Prismari") {
    manaCost = "{2}{U}{R}"
    colorIdentity = "RU"
    typeLine = "Legendary Creature — Elder Dragon"
    oracleText =
        "Flying\n" +
        "When Galazeth Prismari enters, create a Treasure token.\n" +
        "Artifacts you control have \"{T}: Add one mana of any color. Spend this mana only to cast an instant or sorcery spell.\""
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateTreasure(1)
    }

    staticAbility {
        ability = GrantActivatedAbility(
            ability = ActivatedAbility(
                cost = Costs.Tap,
                effect = Effects.AddManaOfChoice(restriction = ManaRestriction.InstantOrSorceryOnly),
                timing = TimingRule.ManaAbility,
                isManaAbility = true
            ),
            filter = GroupFilter(GameObjectFilter.Artifact.youControl())
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "189"
        artist = "Raymond Swanland"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/06c9158c-064b-4d12-b860-d2c1450d1897.jpg?1783927311"
    }
}
