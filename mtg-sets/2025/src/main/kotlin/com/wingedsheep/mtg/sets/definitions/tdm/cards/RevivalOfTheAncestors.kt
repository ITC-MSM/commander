package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.model.Rarity

/**
 * Revival of the Ancestors
 * {1}{W}{B}{G}
 * Enchantment — Saga
 *
 * (As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)
 * I — Create three 1/1 white Spirit creature tokens.
 * II — Distribute three +1/+1 counters among one, two, or three target creatures you control.
 * III — Creatures you control gain trample and lifelink until end of turn.
 *
 * Chapter I creates three Spirit tokens. Chapter II declares one-to-three "you control" creature
 * targets and uses [Effects.DistributeCountersAmongTargets] (total = 3) so the player allocates
 * the +1/+1 counters across the chosen creatures at resolution. Chapter III grants trample and
 * lifelink to every creature you control until end of turn via two
 * [Patterns.Group.grantKeywordToAll] over [GroupFilter.AllCreaturesYouControl].
 */
val RevivalOfTheAncestors = card("Revival of the Ancestors") {
    manaCost = "{1}{W}{B}{G}"
    colorIdentity = "WBG"
    typeLine = "Enchantment — Saga"
    oracleText = "(As this Saga enters and after your draw step, add a lore counter. Sacrifice after III.)\n" +
        "I — Create three 1/1 white Spirit creature tokens.\n" +
        "II — Distribute three +1/+1 counters among one, two, or three target creatures you control.\n" +
        "III — Creatures you control gain trample and lifelink until end of turn."

    sagaChapter(1) {
        effect = Effects.CreateToken(
            count = 3,
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            imageUri = "https://cards.scryfall.io/normal/front/f/2/f22410b3-5c0b-4282-9b0b-5ba61229b6e7.jpg?1743176224"
        )
    }

    sagaChapter(2) {
        target(
            "one, two, or three target creatures you control",
            TargetCreature(
                count = 3,
                minCount = 1,
                filter = TargetFilter(GameObjectFilter.Creature.youControl())
            )
        )
        effect = Effects.DistributeCountersAmongTargets(totalCounters = 3)
    }

    sagaChapter(3) {
        effect = Patterns.Group.grantKeywordToAll(Keyword.TRAMPLE, GroupFilter.AllCreaturesYouControl)
            .then(Patterns.Group.grantKeywordToAll(Keyword.LIFELINK, GroupFilter.AllCreaturesYouControl))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "218"
        artist = "Clint Lockwood"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd742ff5-f0ea-4f4b-911e-4c09e2154dba.jpg?1744578010"
    }
}
