package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.LifeLossFloor
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Ali from Cairo
 * {2}{R}{R}
 * Creature — Human
 * 0/1
 *
 * Damage that would reduce your life total to less than 1 reduces it to 1 instead.
 *
 * Implementation note: this is a life-loss floor replacement on the damage-as-life-loss
 * pipeline (CR 120.3a). The damage event itself still fires at the full amount — only
 * the life-total reduction is capped at 1 — so lifelink, damage-dealt triggers, and
 * abilities keying off the dealt damage still see the original amount. Direct
 * life-loss effects (pay-life costs, Drain Life, Greed) are unaffected.
 */
val AliFromCairo = card("Ali from Cairo") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human"
    power = 0
    toughness = 1
    oracleText = "Damage that would reduce your life total to less than 1 reduces it to 1 instead."

    replacementEffect(
        LifeLossFloor(
            floor = 1,
            appliesTo = EventPattern.LifeLossEvent(player = Player.You),
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "36"
        artist = "Mark Poole"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42027613-d261-4ce2-8ba1-7a2480c660f8.jpg?1562907105"

        ruling("2004-10-04", "This effect does not apply to effects which reduce your life without doing damage.")
        ruling("2004-10-04", "The ability works up until Ali enters the graveyard, so if he takes lethal damage or is destroyed at the same time you take damage, the ability helps you. If the damage occurs after it goes to the graveyard, however, it is not affected by the Ali which is no longer on the battlefield.")
        ruling("2004-10-04", "This effect does not prevent damage, it prevents the damage from turning into loss of life. So the full damage is dealt (and abilities that trigger on damage being dealt still trigger), but the full loss of life is not applied.")
    }
}
