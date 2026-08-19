package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.solvedStaticAbility
import com.wingedsheep.sdk.dsl.toSolve
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Case of the Gorgon's Kiss — Murders at Karlov Manor #79
 * {B} · Enchantment — Case · Uncommon
 *
 * When this Case enters, destroy up to one target creature that was dealt damage this turn.
 * To solve — Three or more creature cards were put into graveyards from anywhere this turn.
 * Solved — This Case is a 4/4 Gorgon creature with deathtouch and lifelink in addition to its
 * other types.
 *
 * "Up to one target" is `optional = true`, not a mandatory target: a board where nothing has been
 * damaged still lets the Case enter and its trigger resolve doing nothing.
 *
 * The "to solve" count is game-wide — "put into graveyard**s**", not "your graveyard" — so it is
 * [Conditions.CreatureCardsPutIntoGraveyardsThisTurn], which sums every player's tracker. Two
 * printed rulings fall out of how that tracker works: it reads the card's own type line, i.e. what
 * the card *is in the graveyard*, so a creature card that had become a noncreature permanent still
 * counts and an animated noncreature card does not; and tokens never count, because a token isn't
 * a card (CR 111.6).
 *
 * The Solved line is five statics rather than one animate effect, the same decomposition Kaito
 * uses: add the CREATURE type ("in addition to its other types", so nothing is removed), add the
 * Gorgon subtype, set base power and toughness, and grant the two keywords. Written as continuous
 * abilities they switch off again the moment the Case stops being solved — which can only happen
 * by it leaving the battlefield, but it is also what keeps a copy of this Case a plain enchantment.
 */
val CaseOfTheGorgonsKiss = card("Case of the Gorgon's Kiss") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Case"
    oracleText = "When this Case enters, destroy up to one target creature that was dealt damage " +
        "this turn.\n" +
        "To solve — Three or more creature cards were put into graveyards from anywhere this " +
        "turn. (If unsolved, solve at the beginning of your end step.)\n" +
        "Solved — This Case is a 4/4 Gorgon creature with deathtouch and lifelink in addition to " +
        "its other types."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target = TargetCreature(
            optional = true,
            filter = TargetFilter(GameObjectFilter.Creature.wasDealtDamageThisTurn())
        )
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
    }

    toSolve(Conditions.CreatureCardsPutIntoGraveyardsThisTurn(3))

    solvedStaticAbility { ability = GrantCardType("CREATURE", GroupFilter.source()) }
    solvedStaticAbility { ability = GrantSubtype("Gorgon", GroupFilter.source()) }
    solvedStaticAbility { ability = SetBasePowerToughnessStatic(4, 4, GroupFilter.source()) }
    solvedStaticAbility { ability = GrantKeyword(Keyword.DEATHTOUCH, GroupFilter.source()) }
    solvedStaticAbility { ability = GrantKeyword(Keyword.LIFELINK, GroupFilter.source()) }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "79"
        artist = "Peter Polach"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45e4c07a-3205-4193-8163-b0e63e6242a4.jpg?1783912902"

        ruling(
            "2024-02-09",
            "The \"to solve\" ability of Case of the Gorgon's Kiss looks at what type the cards " +
                "are after they move to the graveyard to determine whether the ability should " +
                "trigger, regardless of any types they may have had before they were in the " +
                "graveyard."
        )
        ruling(
            "2024-02-09",
            "Tokens are not cards and, as such, do not count toward Case of the Gorgon's Kiss " +
                "\"to solve\" ability."
        )
    }
}
