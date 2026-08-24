package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Ezuri's Brigade — Scars of Mirrodin #121
 * {2}{G}{G} · Creature — Elf Warrior · 4 / 4
 *
 * Metalcraft — As long as you control three or more artifacts, this creature gets +4/+4 and has
 * trample.
 *
 * "Metalcraft" is an ability word (CR 207.2c) with no rules meaning of its own — there is no
 * `Keyword.METALCRAFT`, only the oracle line. The printed sentence hands out two effects in two
 * different layers — [ModifyStats] in 7c, [GrantKeyword] in 6 — so it is two
 * [ConditionalStaticAbility]s over the same [Conditions.YouControlAtLeast] gate rather than one.
 * The Brigade is not itself an artifact, so it needs three others.
 */
val EzurisBrigade = card("Ezuri's Brigade") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Warrior"
    power = 4
    toughness = 4
    oracleText = "Metalcraft — As long as you control three or more artifacts, this creature gets +4/+4 and has trample."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(powerBonus = 4, toughnessBonus = 4, filter = GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact),
        )
    }

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source()),
            condition = Conditions.YouControlAtLeast(3, GameObjectFilter.Artifact),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "121"
        artist = "Nic Klein"
        flavorText = "Riding ravenous, ever-growing vorracs is almost as dangerous as fitting them with saddles."
        imageUri = "https://cards.scryfall.io/normal/front/0/7/079a6b44-3492-4484-aed1-5cd2449e702d.jpg?1783941718"
    }
}
