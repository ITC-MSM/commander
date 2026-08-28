package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CompositeStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Sigarda's Summons
 * {4}{W}{W}
 * Enchantment
 *
 * Creatures you control with +1/+1 counters on them have base power and toughness 4/4, have
 * flying, and are Angels in addition to their other types.
 *
 * One printed ability spanning three layers — Layer 4 (the Angel subtype), Layer 6 (flying) and
 * Layer 7b (base P/T) — so it is a single [CompositeStaticAbility] rather than three
 * `staticAbility { }` blocks (CR 613.6, the Archon of the Wild Rose shape). Split into three
 * blocks the engine would treat each layer as its own effect and re-resolve the affected set per
 * layer; bundled, the set is locked once and every layer applies to that same set.
 *
 * [GrantSubtype] defaults its filter to the *source*, so the shared filter must be passed
 * explicitly here or the enchantment would quietly make itself an Angel.
 *
 * The +1/+1 counters themselves still apply on top: they are Layer 7d, above the 7b base-P/T set,
 * so a 1/1 with two counters ends up 6/6 rather than 4/4. And because the filter reads counters
 * live, removing the last counter (or the enchantment leaving) reverts the creature mid-combat,
 * which is exactly what the printed ruling describes.
 */
val SigardasSummons = card("Sigarda's Summons") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Creatures you control with +1/+1 counters on them have base power and toughness " +
        "4/4, have flying, and are Angels in addition to their other types."

    val countered = GroupFilter(
        GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE)
    )

    staticAbility {
        ability = CompositeStaticAbility(
            listOf(
                SetBasePowerToughnessStatic(4, 4, countered),
                GrantKeyword(Keyword.FLYING, countered),
                GrantSubtype(Subtype.ANGEL.value, countered),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "36"
        artist = "Néstor Ossandón Leal"
        flavorText = "With Olivia distracted, Sigarda tore free from her bindings and summoned " +
            "her Host of Herons."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a27f647a-d48e-4f53-ae5b-64c76f4fc745.jpg?1783924908"

        ruling(
            "2021-11-19",
            "Removing all +1/+1 counters from a creature affected by this ability or removing " +
                "Sigarda's Summons from the battlefield will cause that creature to revert back " +
                "to what it was. Notably, this may cause a creature to lose flying after it has " +
                "been declared as an attacker but before blockers are declared. This may also " +
                "result in a creature with damage already marked on it being destroyed if that " +
                "damage is greater than the creature's toughness."
        )
    }
}
