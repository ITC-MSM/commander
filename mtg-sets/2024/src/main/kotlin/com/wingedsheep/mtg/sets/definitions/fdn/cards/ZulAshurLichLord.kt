package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Zul Ashur, Lich Lord
 * {1}{B}
 * Legendary Creature — Zombie Warlock
 * 2/2
 *
 * Ward—Pay 2 life.
 * {T}: You may cast target Zombie creature card from your graveyard this turn.
 *
 * Implementation:
 * - Ward is the printed [KeywordAbility.wardLife] (counter unless the targeting opponent pays 2 life).
 * - The activated ability gathers the chosen Zombie-creature card ([CardSource.ChosenTargets]) and
 *   grants a may-play-from-graveyard permission ([GrantMayPlayFromExileEffect]) that expires at end
 *   of turn. The card is not moved out of the graveyard — the cast enumerator already honours a
 *   `MayPlayPermission` on a graveyard card (Tinybones, the Pickpocket). No `withAnyManaType` and no
 *   flash grant, so the card is cast for its normal cost following normal timing rules, matching the
 *   Scryfall ruling ("You pay all costs and follow all normal timing rules for Zombie creature cards
 *   cast with the permission"). The "may" is the permission itself — the player chooses whether to cast.
 */
val ZulAshurLichLord = card("Zul Ashur, Lich Lord") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Zombie Warlock"
    power = 2
    toughness = 2
    oracleText = "Ward—Pay 2 life. (Whenever this creature becomes the target of a spell or " +
        "ability an opponent controls, counter it unless that player pays 2 life.)\n" +
        "{T}: You may cast target Zombie creature card from your graveyard this turn."

    keywordAbility(KeywordAbility.wardLife(2))

    activatedAbility {
        cost = Costs.Tap
        target(
            "target Zombie creature card from your graveyard",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Creature.withSubtype(Subtype.ZOMBIE).ownedByYou(),
                    zone = Zone.GRAVEYARD,
                )
            )
        )
        effect = Effects.Composite(
            listOf(
                GatherCardsEffect(
                    source = CardSource.ChosenTargets,
                    storeAs = "zulAshurTarget",
                ),
                GrantMayPlayFromExileEffect(
                    from = "zulAshurTarget",
                    expiry = MayPlayExpiry.EndOfTurn,
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "77"
        artist = "Raluca Marinescu"
        imageUri = "https://cards.scryfall.io/normal/front/3/4/34ad4fdb-9805-45b3-ba20-e47a15d6ff38.jpg?1783909107"

        ruling("2024-11-08", "You pay all costs and follow all normal timing rules for Zombie " +
            "creature cards cast with the permission granted by Zul Ashur's last ability.")
    }
}
