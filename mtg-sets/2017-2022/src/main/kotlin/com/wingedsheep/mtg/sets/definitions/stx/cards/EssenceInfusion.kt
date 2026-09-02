package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Essence Infusion — Strixhaven: School of Mages #69 (canonical printing)
 * {1}{B} · Sorcery
 *
 * Put two +1/+1 counters on target creature. It gains lifelink until end of turn.
 *
 * One target, two clauses in printed order: [Effects.AddCounters] of two +1/+1 counters, `then`
 * [Effects.GrantKeyword] of lifelink with the default until-end-of-turn duration. The counters are
 * permanent; only the lifelink expires.
 */
val EssenceInfusion = card("Essence Infusion") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText =
        "Put two +1/+1 counters on target creature. It gains lifelink until end of turn."

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, creature) then
            Effects.GrantKeyword(Keyword.LIFELINK, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "69"
        artist = "Randy Vargas"
        flavorText = "\"I'd say it's still a fair fight—now my friend is about the size of your ego.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71a48d0c-1e5b-43f2-8974-e2e5bb06310d.jpg?1783927368"
    }
}
