package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Kaalia of the Vast
 * {1}{R}{W}{B}
 * Legendary Creature — Human Cleric
 * 2/2
 *
 * Flying
 * Whenever Kaalia of the Vast attacks an opponent, you may put an Angel, Demon, or Dragon
 * creature card from your hand onto the battlefield tapped and attacking that opponent.
 *
 * Engine notes:
 * - "attacks an opponent" is the whole reason for engine feature #1258. A creature can be
 *   declared as attacking a player, a planeswalker, or a battle (CR 508.1); Kaalia's ability
 *   fires only for the *player* case. The 2024-06-07 ruling makes this explicit: "Kaalia's
 *   ability doesn't trigger if it attacks a planeswalker or battle." That gate is
 *   `Triggers.AttacksAnOpponent` (SELF + `AttackPredicate.DefenderIsPlayer`), NOT the
 *   unfiltered `Triggers.Attacks`.
 * - "put an Angel, Demon, or Dragon creature card from your hand ... tapped and attacking"
 *   reuses `Patterns.Hand.putFromHand(entersAttacking = true)`: GatherCards (hand) →
 *   SelectFromCollection (ChooseUpTo 1 = "you may") → MoveCollection with
 *   `ZonePlacement.TappedAndAttacking`, which adds the `AttackingComponent` against the
 *   defending player. The subtype gate is
 *   `GameObjectFilter.Creature.withAnySubtype("Angel", "Demon", "Dragon")`.
 * - "that opponent": the two-player engine has a single opponent, and the tapped-and-attacking
 *   placement attacks the first opponent in turn order — i.e. the player Kaalia attacked. The
 *   multiplayer "that opponent" nuance is a pre-existing shared limitation of the
 *   entersAttacking pipeline, not specific to Kaalia.
 */
val KaaliaOfTheVast = card("Kaalia of the Vast") {
    manaCost = "{1}{R}{W}{B}"
    colorIdentity = "BRW"
    typeLine = "Legendary Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Whenever Kaalia of the Vast attacks an opponent, you may put an Angel, Demon, or Dragon " +
        "creature card from your hand onto the battlefield tapped and attacking that opponent."

    keywords(Keyword.FLYING)

    // Whenever Kaalia attacks an *opponent* (a player, not a planeswalker or battle), you may put
    // an Angel, Demon, or Dragon creature card from your hand onto the battlefield tapped and
    // attacking that opponent.
    triggeredAbility {
        trigger = Triggers.AttacksAnOpponent
        effect = Patterns.Hand.putFromHand(
            filter = GameObjectFilter.Creature.withAnySubtype("Angel", "Demon", "Dragon"),
            entersAttacking = true
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "206"
        artist = "Michael Komarck"
        flavorText = "I'll have my revenge if I have to call on every force from above and below."
        imageUri = "https://cards.scryfall.io/normal/front/4/b/4b71d89b-7ba4-406f-8736-ac62b9864f21.jpg?1782715001"

        ruling("2020-08-07", "Kaalia of the Vast's ability triggers only if it attacks a player. It won't trigger if it attacks a planeswalker.")
        ruling("2024-06-07", "Kaalia of the Vast's ability doesn't trigger if it attacks a planeswalker or battle.")
        ruling("2020-08-07", "The creature card is put onto the battlefield tapped and attacking the player Kaalia is attacking. It wasn't declared as an attacking creature, so abilities that trigger whenever a creature attacks won't trigger.")
        ruling("2020-08-07", "The creature is attacking, but it was never declared as an attacker. This means that any \"whenever this creature attacks\" abilities of that creature won't trigger.")
        ruling("2020-08-07", "Because the creature is put onto the battlefield attacking, it's never been declared as an attacker. Effects that care about attacking creatures being declared won't apply.")
    }
}
