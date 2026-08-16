package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Archon of Sun's Grace
 * {2}{W}{W}
 * Creature — Archon
 * 3/4
 * Flying
 * Lifelink (Damage dealt by this creature also causes you to gain that much life.)
 * Pegasus creatures you control have lifelink.
 * Constellation — Whenever an enchantment you control enters, create a 2/2 white Pegasus creature token with flying.
 */
val ArchonOfSunsGrace = card("Archon of Sun's Grace") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Archon"
    power = 3
    toughness = 4
    oracleText = "Flying\nLifelink (Damage dealt by this creature also causes you to gain that much life.)\nPegasus creatures you control have lifelink.\nConstellation — Whenever an enchantment you control enters, create a 2/2 white Pegasus creature token with flying."

    keywords(Keyword.FLYING, Keyword.LIFELINK)

    // Pegasus creatures you control have lifelink.
    staticAbility {
        ability = GrantKeyword(
            Keyword.LIFELINK,
            GroupFilter(GameObjectFilter.Creature.withSubtype("Pegasus").youControl())
        )
    }

    // Constellation — Whenever an enchantment you control enters, create a 2/2 white Pegasus
    // creature token with flying. "Constellation" is an ability word: no rules meaning of its own.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Pegasus"),
            keywords = setOf(Keyword.FLYING),
        )
        description = "Constellation — Whenever an enchantment you control enters, create a 2/2 " +
            "white Pegasus creature token with flying."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "3"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/235e5999-e8e5-4093-adff-9d47aec70d10.jpg?1783931603"
        ruling("2020-01-24", "Multiple instances of lifelink on the same creature are redundant.")
        ruling("2020-01-24", "A constellation ability triggers whenever an enchantment enters the battlefield under your control for any reason. Enchantments with other card types, such as enchantment creatures, will also cause constellation abilities to trigger.")
        ruling("2020-01-24", "An Aura spell that has an illegal target when it tries to resolve doesn't resolve and is instead put into its owner's graveyard. It doesn't enter the battlefield, so constellation abilities don't trigger.")
    }
}
