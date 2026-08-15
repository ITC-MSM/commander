package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sanguine Evangelist
 * {2}{W}
 * Creature — Vampire Cleric
 * 2/1
 *
 * Battle cry (Whenever this creature attacks, each other attacking creature gets +1/+0 until
 * end of turn.)
 * When this creature enters or dies, create a 1/1 black Bat creature token with flying.
 *
 * Battle cry is not a distinct engine keyword; it is modeled from its reminder text as a
 * `Triggers.Attacks` (SELF) ability that pumps every OTHER attacking creature. The mass buff
 * uses `ForEachInGroup` over the group of attacking creatures (excluding this one) so each
 * attacker receives its own +1/+0 floating effect — a `GroupRef` target on `ModifyStats` is
 * not expanded per-permanent (The Wind Crystal idiom). The filter is unrestricted by
 * controller ("each other attacking creature", CR 702.91a).
 *
 * "Enters or dies" is two triggered abilities (enters + dies) sharing one effect, mirroring
 * Queen's Bay Paladin's enters/attacks split. The 1/1 black flying Bat reuses the Bloomburrow
 * Bat token art (no distinct Lost Caverns of Ixalan Bat token is modeled in the repo).
 */
val SanguineEvangelist = card("Sanguine Evangelist") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Vampire Cleric"
    power = 2
    toughness = 1
    oracleText = "Battle cry (Whenever this creature attacks, each other attacking creature gets " +
        "+1/+0 until end of turn.)\n" +
        "When this creature enters or dies, create a 1/1 black Bat creature token with flying."

    // Battle cry: whenever this creature attacks, each other attacking creature gets +1/+0 UEOT.
    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.ForEachInGroup(
            GroupFilter(
                baseFilter = GameObjectFilter.Creature.attacking(),
                excludeSelf = true,
            ),
            Effects.ModifyStats(1, 0, EffectTarget.Self),
        )
    }

    // When this creature enters, create a 1/1 black Bat with flying.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Bat"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/0/0/00841e4b-0995-4fb5-93d6-e177beba4934.jpg?1783913608",
        )
    }

    // When this creature dies, create a 1/1 black Bat with flying.
    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Bat"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/0/0/00841e4b-0995-4fb5-93d6-e177beba4934.jpg?1783913608",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "34"
        artist = "Zezhou Chen"
        flavorText = "\"Fang-brethren, we feed together.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/6/269ddd84-fdc4-4c94-b183-32ecec56967c.jpg?1782694583"
    }
}
