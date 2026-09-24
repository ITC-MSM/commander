package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Master Warcraft
 * {2}{R/W}{R/W}
 * Instant
 *
 * Cast this spell only before attackers are declared.
 * You choose which creatures attack this turn.
 * You choose which creatures block this turn and how those creatures block.
 *
 * [Effects.ChooseAttackersAndBlockersThisTurn] hands every attack and block *declaration* of the
 * turn to the caster — in every combat phase, whichever side the creatures are on — and nothing
 * else: priority, other decisions and hidden cards stay with their owners, and the declarations are
 * validated exactly as if their controllers had made them (ruling: "your choices must be legal
 * within the normal rules for attacking and blocking"). Declining to attack or block is a legal
 * choice. The timing line is [Conditions.BeforeAttackersDeclared]: before the declare attackers step
 * of the turn's first combat phase (2013 ruling).
 *
 * Known gap: the 2008 ruling splits attacking into two choices when the defending player controls
 * a planeswalker — the caster picks *which* creatures attack, the active player picks *what* each
 * attacks. Here one `DeclareAttackers` carries both, so the caster also picks the attack targets.
 */
val MasterWarcraft = card("Master Warcraft") {
    manaCost = "{2}{R/W}{R/W}"
    colorIdentity = "RW"
    typeLine = "Instant"
    oracleText = "Cast this spell only before attackers are declared.\n" +
        "You choose which creatures attack this turn.\n" +
        "You choose which creatures block this turn and how those creatures block."

    spell {
        castOnlyIf(Conditions.BeforeAttackersDeclared)
        effect = Effects.ChooseAttackersAndBlockersThisTurn()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "250"
        artist = "Zoltan Boros & Gabor Szikszai"
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d111510-ca7f-4127-b540-48265aa36311.jpg?1783943603"
        ruling("2013-09-20", "If a turn has multiple combat phases, this spell can only be cast before the beginning of the Declare Attackers Step of the first combat phase in that turn.")
        ruling("2008-04-01", "If the defending player controls a planeswalker, the person who cast Master Warcraft first chooses the complete group of creatures that are going to attack. Then, for each of those creatures, the active player chooses who or what it's going to attack.")
        ruling("2006-01-01", "You can decide that a creature won't block.")
        ruling("2005-10-01", "You choose attackers and make blocking assignments regardless of whether it's your turn and regardless of whether the creatures are attacking you. Your choices must be legal within the normal rules for attacking and blocking.")
    }
}
