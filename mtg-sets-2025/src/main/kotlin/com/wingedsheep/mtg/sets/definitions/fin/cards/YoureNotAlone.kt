package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * You're Not Alone
 * {W}
 * Instant
 * Target creature gets +2/+2 until end of turn. If you control three or more creatures, it gets
 * +4/+4 until end of turn instead.
 *
 * The "instead" swaps the buff amount based on how many creatures you control at resolution; both
 * branches buff the same target until end of turn.
 */
val YoureNotAlone = card("You're Not Alone") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 until end of turn. If you control three or more creatures, it gets +4/+4 until end of turn instead."

    spell {
        val t = target("target", Targets.Creature)
        effect = ConditionalEffect(
            condition = Conditions.ControlCreaturesAtLeast(3),
            effect = Effects.ModifyStats(4, 4, t),
            elseEffect = Effects.ModifyStats(2, 2, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Ignatius Budi"
        flavorText = "\"You've always protected us. But you still don't understand that we looked out for you, too!\"\n—Dagger"
        imageUri = "https://cards.scryfall.io/normal/front/1/8/1867b5cb-2bb0-4f49-b302-036fdffa2344.jpg?1748705918"
    }
}
