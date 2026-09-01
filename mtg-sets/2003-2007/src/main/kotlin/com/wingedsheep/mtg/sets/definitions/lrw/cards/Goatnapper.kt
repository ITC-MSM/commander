package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Goatnapper
 * {2}{R}
 * Creature — Goblin Rogue
 * 2/2
 * When this creature enters, untap target Goat and gain control of it until end of turn.
 * It gains haste until end of turn.
 *
 * The Threaten pattern hung off an enters trigger: the same
 * `Untap` + `GainControl(EndOfTurn)` + `GrantKeyword(HASTE)` composite Blind with Anger uses,
 * with the target narrowed to a Goat.
 *
 * "Target Goat" is any Goat on the battlefield, not just an opponent's — stealing your own Goat
 * back from nobody is legal (and pointless), and the untap/haste half still applies. Lorwyn's own
 * Goats are Cloudgoat Ranger's tokens and Springjack Pasture's Goats, but the changeling cycle
 * makes every changeling a legal target too, which is exactly why the filter is a subtype test on
 * *projected* state rather than a printed-type read.
 */
val Goatnapper = card("Goatnapper") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Rogue"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, untap target Goat and gain control of it until end of turn. " +
        "It gains haste until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val goat = target(
            "target Goat",
            TargetCreature(filter = TargetFilter.Creature.withSubtype(Subtype.GOAT))
        )
        effect = Effects.Composite(
            Effects.Untap(goat),
            Effects.GainControl(goat, Duration.EndOfTurn),
            Effects.GrantKeyword(Keyword.HASTE, goat)
        )
        description = "untap target Goat and gain control of it until end of turn. " +
            "It gains haste until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "172"
        artist = "Ron Spencer"
        flavorText = "Kith goats are just for practice. The real prize, of course, is a giant's cloudgoat."
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78f32852-2a18-453d-910a-829c3a2b5b1b.jpg?1783942876"
    }
}
