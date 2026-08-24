package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fanatical Fever
 * {2}{G}{G}
 * Instant
 *
 * Target creature gets +3/+0 and gains trample until end of turn.
 *
 * One target shared by two effects — the pump and the keyword grant both point at the same bound
 * variable — so it is a `Composite` over one requirement rather than two separate clauses. Both
 * primitives already default to `Duration.EndOfTurn`, which is what "until end of turn" means here.
 */
val FanaticalFever = card("Fanatical Fever") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+0 and gains trample until end of turn."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 0, t),
            Effects.GrantKeyword(Keyword.TRAMPLE, t)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "234"
        artist = "Julie Baroh"
        flavorText = "\"Let go your fury, and hone your anger. Become the fist of Freyalise!\"\n—Kolbjörn, Elder Druid of the Juniper Order"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2abba7f1-5d07-4137-88a2-5967396a3e42.jpg"
    }
}
