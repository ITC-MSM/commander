package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Nectar Faerie
 * {1}{B}
 * Creature — Faerie Wizard
 * 1/1
 * Flying
 * {B}, {T}: Target Faerie or Elf gains lifelink until end of turn.
 *
 * The target names no card type and no controller: it is any *permanent* that is a Faerie or an
 * Elf, an opponent's included, and in Lorwyn that reaches the Kindred noncreature permanents
 * (a Kindred Enchantment — Faerie is a Faerie) as well as creatures. So the filter is
 * [GameObjectFilter.Permanent] with a [GameObjectFilter.withAnySubtype] union, not
 * `GameObjectFilter.Creature` — one noun phrase, one filter, the way Goblin Wizard's
 * "target Goblin" is written.
 */
val NectarFaerie = card("Nectar Faerie") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Faerie Wizard"
    power = 1
    toughness = 1
    oracleText = "Flying\n" +
        "{B}, {T}: Target Faerie or Elf gains lifelink until end of turn. (Damage dealt by the " +
        "creature also causes its controller to gain that much life.)"

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}"), Costs.Tap)
        val t = target(
            "target Faerie or Elf",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Permanent.withAnySubtype(Subtype.FAERIE.value, Subtype.ELF.value)
                )
            )
        )
        effect = Effects.GrantKeyword(Keyword.LIFELINK, t)
        description = "{B}, {T}: Target Faerie or Elf gains lifelink until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "130"
        artist = "Thomas Denmark"
        flavorText = "\"The unpredictable fae are just as likely to provide a blight as a boon.\"\n" +
            "—Desmera, perfect of Wren's Run"
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d943b877-805d-4bc8-a3ae-abec00fa51a6.jpg?1783942885"
    }
}
