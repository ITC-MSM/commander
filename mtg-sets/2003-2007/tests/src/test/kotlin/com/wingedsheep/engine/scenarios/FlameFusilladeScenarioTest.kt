package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.FlameFusillade
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.effects.ForEachEffect
import com.wingedsheep.sdk.scripting.effects.GrantActivatedAbilityEffect
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Flame Fusillade — {3}{R} Sorcery (Ravnica: City of Guilds #123)
 *
 * "Until end of turn, permanents you control gain "{T}: This permanent deals 1 damage to any target.""
 *
 * The card is a `ForEachInGroup` fan-out of a [GrantActivatedAbilityEffect] onto
 * `EffectTarget.Self`, so everything worth proving is about where "Self" lands:
 *  - every permanent you control receives the grant — lands included, and a land that entered this
 *    turn can still pay `{T}` because summoning sickness gates only creatures (the card's ruling);
 *  - the `{T}` taps *that* permanent, and it is the source of the damage (CR 113.7);
 *  - permanents an opponent controls receive nothing, and the grant is gone next turn.
 */
class FlameFusilladeScenarioTest : FunSpec({

    // The pinger granted by the spell's ForEachInGroup body.
    val grantedAbilityId =
        ((FlameFusillade.spellEffect as ForEachEffect).body as GrantActivatedAbilityEffect).ability.id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + FlameFusillade)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("every permanent you control gains the pinger — a land can shoot the turn it resolves") {
        val d = driver()
        val fusillade = d.putCardInHand(d.player1, "Flame Fusillade")
        val shooter = d.putLandOnBattlefield(d.player1, "Mountain")

        d.giveMana(d.player1, Color.RED, 1)
        d.giveColorlessMana(d.player1, 3)
        d.castSpell(d.player1, fusillade).error shouldBe null
        d.bothPass()

        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = shooter,
                abilityId = grantedAbilityId,
                targets = listOf(ChosenTarget.Player(d.player2))
            )
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("\"permanents you control\" includes lands, and {T} isn't gated by summoning sickness") {
            d.getLifeTotal(d.player2) shouldBe 19
        }
        withClue("the {T} taps the granted permanent itself") {
            d.state.getEntity(shooter)?.get<TappedComponent>() shouldNotBe null
        }
    }

    test("the granted permanent is the damage source, and opponents' permanents get nothing") {
        val d = driver()
        val fusillade = d.putCardInHand(d.player1, "Flame Fusillade")
        val mine = d.putCreatureOnBattlefield(d.player1, "Centaur Courser") // 3/3
        d.removeSummoningSickness(mine)
        val theirs = d.putCreatureOnBattlefield(d.player2, "Savannah Lions") // 1/1
        d.removeSummoningSickness(theirs)

        d.giveMana(d.player1, Color.RED, 1)
        d.giveColorlessMana(d.player1, 3)
        d.castSpell(d.player1, fusillade).error shouldBe null
        d.bothPass()

        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = mine,
                abilityId = grantedAbilityId,
                targets = listOf(ChosenTarget.Permanent(theirs))
            )
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("1 damage is lethal to a 1/1, so the damage demonstrably landed") {
            d.findPermanent(d.player2, "Savannah Lions") shouldBe null
        }
        withClue("\"permanents you control\" — the opponent's board was never granted anything") {
            val alsoTheirs = d.putCreatureOnBattlefield(d.player2, "Savannah Lions")
            d.removeSummoningSickness(alsoTheirs)
            d.submit(
                ActivateAbility(
                    playerId = d.player2,
                    sourceId = alsoTheirs,
                    abilityId = grantedAbilityId,
                    targets = listOf(ChosenTarget.Player(d.player1))
                )
            ).isSuccess shouldBe false
            d.getLifeTotal(d.player1) shouldBe 20
        }
    }

    test("the grant expires at end of turn") {
        val d = driver()
        val fusillade = d.putCardInHand(d.player1, "Flame Fusillade")
        val shooter = d.putLandOnBattlefield(d.player1, "Mountain")

        d.giveMana(d.player1, Color.RED, 1)
        d.giveColorlessMana(d.player1, 3)
        d.castSpell(d.player1, fusillade).error shouldBe null
        d.bothPass()

        // Round the table back to player 1's own main phase, so priority isn't the reason it fails.
        // (Step the cursor off PRECOMBAT_MAIN first, or the passes are a no-op.)
        d.passPriorityUntil(Step.UPKEEP)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        d.passPriorityUntil(Step.UPKEEP)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.submit(
            ActivateAbility(
                playerId = d.player1,
                sourceId = shooter,
                abilityId = grantedAbilityId,
                targets = listOf(ChosenTarget.Player(d.player2))
            )
        ).isSuccess shouldBe false
        d.getLifeTotal(d.player2) shouldBe 20
    }
})
