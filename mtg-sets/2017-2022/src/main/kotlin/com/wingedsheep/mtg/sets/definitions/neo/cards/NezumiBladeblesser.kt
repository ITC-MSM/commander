package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Nezumi Bladeblesser — Kamigawa: Neon Dynasty #115 (canonical printing)
 * {2}{B} · Creature — Rat Samurai · 3/2
 *
 * This creature has deathtouch as long as you control an artifact.
 * This creature has menace as long as you control an enchantment.
 *
 * Two independent [ConditionalStaticAbility] grants over [GroupFilter.source] — NEO's artifact /
 * enchantment split rewarded playing both halves, and each line rechecks its own condition on its
 * own, so losing the artifact drops deathtouch without touching menace.
 */
val NezumiBladeblesser = card("Nezumi Bladeblesser") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Rat Samurai"
    power = 3
    toughness = 2
    oracleText = "This creature has deathtouch as long as you control an artifact.\n" +
        "This creature has menace as long as you control an enchantment. (It can't be blocked " +
        "except by two or more creatures.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.DEATHTOUCH, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Artifact),
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.MENACE, GroupFilter.source()),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Enchantment),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "Ilse Gort"
        flavorText = "\"It's not one or the other, kami or technology. Both offer power. I will " +
            "not limit myself.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a03d3f5-b32b-4814-b485-25f24e51609c.jpg?1783923878"
    }
}
