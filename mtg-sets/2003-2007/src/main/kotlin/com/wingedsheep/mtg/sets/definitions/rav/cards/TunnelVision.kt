package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.ConditionalOnCollectionEffect
import com.wingedsheep.sdk.scripting.effects.GatherUntilMatchEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.RevealCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectFromCollectionEffect
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ShuffleLibraryEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetPlayer

/**
 * Tunnel Vision — Ravnica: City of Guilds #72 (canonical printing)
 * {5}{U} · Sorcery
 *
 * Choose a card name. Target player reveals cards from the top of their library until a card with
 * that name is revealed. If it is, that player puts the rest of the revealed cards into their
 * graveyard and puts the card with the chosen name on top of their library. Otherwise, the player
 * shuffles.
 *
 * The Spoils of the Vault walk aimed at someone else: [Effects.ChooseCardName] stores the name and
 * [GameObjectFilter.namedFromVariable] reads it back both as the stopper for
 * [GatherUntilMatchEffect] and as the partition that separates the named card from the pile above it.
 *
 * Two details are worth stating because they are what the card actually does rather than what it
 * says:
 *
 *  - **"Puts the card with the chosen name on top of their library" needs no step.** The revealed
 *    pile is exactly the run from the top of the library down to and including the match, and
 *    nothing here moves the match. Binning everything above it leaves it on top by construction.
 *  - **The "otherwise" branch is load-bearing.** When the name isn't in the library the walk runs
 *    the library out, so the remainder pile is the *whole library*; moving it would be catastrophic
 *    rather than merely wrong. [ConditionalOnCollectionEffect] on the match pile is what keeps the
 *    two branches apart — the 2005-10-01 ruling is explicit that a miss bins nothing and shuffles.
 *
 * Choosing a name that happens to sit on top is the same code path with an empty remainder pile.
 */
val TunnelVision = card("Tunnel Vision") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Choose a card name. Target player reveals cards from the top of their library " +
        "until a card with that name is revealed. If it is, that player puts the rest of the " +
        "revealed cards into their graveyard and puts the card with the chosen name on top of " +
        "their library. Otherwise, the player shuffles."

    spell {
        val victim = target("target player", TargetPlayer())
        val named = GameObjectFilter.Any.namedFromVariable("chosenName")

        effect = Effects.Composite(
            listOf(
                // 1. Name a card. Any name is legal, basic lands included.
                Effects.ChooseCardName(
                    storeAs = "chosenName",
                    prompt = "Choose a card name"
                ),
                // 2. Walk the target's library from the top until that name shows up (or it runs out).
                GatherUntilMatchEffect(
                    player = Player.TargetPlayer,
                    filter = named,
                    storeMatch = "match",
                    storeRevealed = "revealed"
                ),
                RevealCollectionEffect(from = "revealed"),
                // 3. Split the reveal: the named card vs. everything seen on the way down to it.
                SelectFromCollectionEffect(
                    from = "revealed",
                    selection = SelectionMode.All,
                    filter = named,
                    storeSelected = "namedCard",
                    storeRemainder = "rest"
                ),
                // 4. Hit: bin the rest, which leaves the named card on top. Miss: shuffle, bin nothing.
                ConditionalOnCollectionEffect(
                    collection = "match",
                    ifNotEmpty = MoveCollectionEffect(
                        from = "rest",
                        destination = CardDestination.ToZone(Zone.GRAVEYARD, Player.TargetPlayer)
                    ),
                    ifEmpty = ShuffleLibraryEffect(target = victim)
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "72"
        artist = "Dany Orizio"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae3eba31-02eb-449a-8a36-899ada5664bc.jpg?1783943676"
        ruling(
            "2005-10-01",
            "If the named card is found, everything from the top of the library down to the named " +
                "card is put into the graveyard and the library isn't shuffled. If the named card " +
                "isn't in the library, no cards are put into the graveyard and the library is shuffled."
        )
    }
}
