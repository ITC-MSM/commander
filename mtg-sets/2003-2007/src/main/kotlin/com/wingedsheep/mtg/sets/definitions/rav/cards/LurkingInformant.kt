package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Lurking Informant — Ravnica: City of Guilds #249
 * {1}{U/B} · Creature — Human Rogue · 1/2
 *
 * ({U/B} can be paid with either {U} or {B}.)
 * {2}, {T}: Look at the top card of target player's library. You may put that card into that
 * player's graveyard.
 *
 * Modelling notes:
 * - `{U/B}` is a plain hybrid pip in `manaCost`; the parser derives mana value 2 and the
 *   blue-and-black colour identity from it.
 * - The "you may" is the *selection*, not a separate yes/no: gathering the top card and offering
 *   `ChooseUpTo(1)` is one decision that both shows the card and asks whether to bin it — the
 *   surveil shape, aimed at another player's library. The remainder goes back on top, so a
 *   declined activation leaves the library exactly as it was.
 * - The look and the choice both belong to this card's controller; the *targeted* player only
 *   supplies the library and the graveyard.
 */
val LurkingInformant = card("Lurking Informant") {
    manaCost = "{1}{U/B}"
    colorIdentity = "UB"
    typeLine = "Creature — Human Rogue"
    power = 1
    toughness = 2
    oracleText = "({U/B} can be paid with either {U} or {B}.)\n" +
        "{2}, {T}: Look at the top card of target player's library. You may put that card into " +
        "that player's graveyard."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        target("target player", Targets.Player)
        effect = Effects.Composite(
            GatherCardsEffect(
                source = CardSource.TopOfLibrary(DynamicAmount.Fixed(1), Player.ContextPlayer(0)),
                storeAs = "peeked"
            ),
            SelectFromCollectionEffect(
                from = "peeked",
                selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(1)),
                storeSelected = "toGraveyard",
                storeRemainder = "toTop",
                selectedLabel = "Put into that player's graveyard",
                remainderLabel = "Leave on top of that player's library"
            ),
            MoveCollectionEffect(
                from = "toGraveyard",
                destination = CardDestination.ToZone(Zone.GRAVEYARD, Player.ContextPlayer(0))
            ),
            MoveCollectionEffect(
                from = "toTop",
                destination = CardDestination.ToZone(
                    Zone.LIBRARY,
                    Player.ContextPlayer(0),
                    placement = ZonePlacement.Top
                )
            )
        )
        description = "{2}, {T}: Look at the top card of target player's library. You may put " +
            "that card into that player's graveyard."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Ron Spears"
        flavorText = "In the undercity, forgetfulness is often encouraged at the point of a blade."
        imageUri = "https://cards.scryfall.io/normal/front/0/1/012a7f5d-f798-4030-86e3-05956487a383.jpg?1783943604"
    }
}
