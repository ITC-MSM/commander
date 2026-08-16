package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Zhalfirin Lancer
 * {2}{W}
 * Creature — Human Knight
 * 3/3
 * Whenever another Knight you control enters, this creature gets +1/+1 and gains vigilance until
 * end of turn.
 *
 * "another **Knight** you control" is a bare tribal noun, so it names every *permanent* with the
 * subtype rather than only creatures (a Knight artifact or Vehicle entering would trigger it) —
 * `GameObjectFilter.Permanent.withSubtype(KNIGHT)`, not `Creature.withSubtype`. `TriggerBinding.OTHER`
 * is the printed "another": the Lancer entering does not trigger itself.
 */
val ZhalfirinLancer = card("Zhalfirin Lancer") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    oracleText = "Whenever another Knight you control enters, this creature gets +1/+1 and gains " +
        "vigilance until end of turn."
    power = 3
    toughness = 3

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.KNIGHT).youControl(),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self) then
            Effects.GrantKeyword(Keyword.VIGILANCE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "45"
        artist = "Nino Vecia"
        flavorText = "The Phyrexians armored themselves against blades, fire, and every conjuration " +
            "they could think of. She brought a war rhino."
        imageUri = "https://cards.scryfall.io/normal/front/2/7/277e5b49-c53f-4bf7-aac0-950d8708b957.jpg?1783917048"
    }
}
