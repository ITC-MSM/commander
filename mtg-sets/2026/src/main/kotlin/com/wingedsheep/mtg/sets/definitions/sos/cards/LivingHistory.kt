package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Living History
 * {1}{R}
 * Enchantment
 *
 * When this enchantment enters, create a 2/2 red and white Spirit creature token.
 * Whenever you attack, if a card left your graveyard this turn, target attacking creature
 * gets +2/+0 until end of turn.
 *
 * The ETB is a straightforward token-maker. The attack ability is an intervening-"if"
 * triggered ability (Rule 603.4): [Conditions.CardsLeftGraveyardThisTurn] is checked both
 * when the trigger would fire and again on resolution, gating the +2/+0 buff on a card
 * having left the controller's graveyard earlier in the turn.
 */
val LivingHistory = card("Living History") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "When this enchantment enters, create a 2/2 red and white Spirit creature token.\n" +
        "Whenever you attack, if a card left your graveyard this turn, target attacking " +
        "creature gets +2/+0 until end of turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.RED, Color.WHITE),
            creatureTypes = setOf("Spirit"),
            imageUri = "https://cards.scryfall.io/normal/front/8/7/877f7ddb-ed70-41a0-b845-d9bf8ac65f9b.jpg?1775828448",
        )
    }

    triggeredAbility {
        trigger = Triggers.YouAttack
        triggerCondition = Conditions.CardsLeftGraveyardThisTurn(1)
        val attacker = target("attacking creature", Targets.AttackingCreature)
        effect = Effects.ModifyStats(2, 0, attacker)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "121"
        artist = "Caroline Gariba"
        flavorText = "Experience is a harsh tutor."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/2028792c-fd60-40d4-bff7-3b82dbe1ffb5.jpg?1775937797"
    }
}
